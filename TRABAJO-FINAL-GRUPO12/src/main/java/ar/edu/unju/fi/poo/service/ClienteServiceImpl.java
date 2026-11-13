package ar.edu.unju.fi.poo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Empresa;
import ar.edu.unju.fi.poo.model.Particular;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ParticularRepository;
import ar.edu.unju.fi.poo.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteRepository clienteRepo;

	@Autowired
	private ParticularRepository particularRepo;

	@Override
	public Cliente registrarCliente(Cliente cliente) {
		
		if (clienteRepo.existsByEmail(cliente.getEmail())) {
			throw new ClienteDuplicadoException("El email ya esta registrado");
		}
		
		if (cliente instanceof Particular) {
			Particular p = (Particular) cliente;
			if (particularRepo.existsByDni(p.getDni())) {
				throw new ClienteDuplicadoException("El DNI ya existe en el sistema");
			}
		}
		
		return clienteRepo.save(cliente);
	}

	@Override
	public List<Cliente> obtenerTodosLosClientes() {
		return (List<Cliente>) clienteRepo.findAll();
	}

	@Override
	public Cliente buscarPorId(String id) {
		return clienteRepo.findById(id).orElse(null);
	}

	@Override
	public void eliminarCliente(String id) {
		clienteRepo.deleteById(id);
	}

	@Override
	public Cliente buscarPorDniOCuit(String documento) {
		
		return null; 
	}

	@Override
	public List<Cliente> buscarPorNombreORazonSocial(String nombre) {
		
		return null;
	}

	

	@Override
	public ClienteResponseDTO mapearADTO(Cliente cliente) {
		ClienteResponseDTO dto = new ClienteResponseDTO();
		
		dto.setId(cliente.getId()); 
		dto.setEmail(cliente.getEmail());
		
		
		if (cliente instanceof Particular) {
			Particular p = (Particular) cliente; // casteo
			dto.setTipoCliente("PARTICULAR");
			dto.setIdentificacion(p.getDni());
			
			
			dto.setNombreORazonSocial(p.getNombre() + " " + p.getApellido());
		} 
		
		else if (cliente instanceof Empresa) {
			Empresa e = (Empresa) cliente; 
			dto.setTipoCliente("EMPRESA");
			
			
			dto.setIdentificacion("Sin CUIT registrado"); 
			dto.setNombreORazonSocial(e.getRazonSocial());
		}
		
		return dto;
	}

	@Override
	public Cliente mapearAEntidad(ClienteRequestDTO dto) {
		if (dto.getTipoCliente() == null) {
			return null;
		}

		
		if (dto.getTipoCliente().equalsIgnoreCase("PARTICULAR")) {
			Particular p = new Particular();
			p.setEmail(dto.getEmail());
			p.setTelefono(dto.getTelefono());
			p.setDireccion(dto.getDireccion());
			p.setDni(dto.getDni());
			p.setNombre(dto.getNombre());
			
			return p;
			
		} 
		
		else if (dto.getTipoCliente().equalsIgnoreCase("EMPRESA")) {
			Empresa e = new Empresa();
			e.setEmail(dto.getEmail());
			e.setTelefono(dto.getTelefono());
			e.setDireccion(dto.getDireccion());
			
			
			e.setRazonSocial(dto.getRazonSocial());
			return e;
		}
		
		return null; 
	}
}