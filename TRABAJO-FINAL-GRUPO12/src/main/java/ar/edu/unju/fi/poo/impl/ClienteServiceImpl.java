package ar.edu.unju.fi.poo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.dto.EstadoCuentaDTO;
import ar.edu.unju.fi.poo.entity.Cliente;
import ar.edu.unju.fi.poo.entity.Cuota;
import ar.edu.unju.fi.poo.entity.Empresa;
import ar.edu.unju.fi.poo.entity.Financiada;
import ar.edu.unju.fi.poo.entity.Particular;
import ar.edu.unju.fi.poo.entity.Venta;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.mapper.ClienteMapper;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ParticularRepository;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

	private final ClienteRepository clienteRepo;
	private final ParticularRepository particularRepo;
	private final VentaRepository ventaRepo;
	private final ClienteMapper clienteMapper;

	@Override
	public ClienteResponseDTO registrarCliente(ClienteRequestDTO dto) {
		Cliente cliente = clienteMapper.toEntity(dto);
		if (cliente == null) {
			throw new IllegalArgumentException("El tipo de cliente o los datos enviados son inválidos.");
		}

		if (clienteRepo.existsByEmail(cliente.getEmail())) {
			throw new ClienteDuplicadoException("El email ya esta registrado");
		}

		if (cliente instanceof Particular p) {
			if (particularRepo.existsByDni(p.getDni())) {
				throw new ClienteDuplicadoException("El DNI ya existe en el sistema");
			}
		}

		Cliente guardado = clienteRepo.save(cliente);
		return clienteMapper.toResponseDTO(guardado);
	}

	@Override
	public List<ClienteResponseDTO> obtenerTodosLosClientes() {
		List<ClienteResponseDTO> respuesta = new ArrayList<>();
		for (Cliente c : clienteRepo.findAll()) {
			respuesta.add(clienteMapper.toResponseDTO(c));
		}
		return respuesta;
	}

	@Override
	public ClienteResponseDTO buscarPorId(String id) {
		Cliente cliente = clienteRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("No se encontró el cliente con ID: " + id));
		return clienteMapper.toResponseDTO(cliente);
	}

	@Override
	public void eliminarCliente(String id) {
		if (!clienteRepo.existsById(id)) {
			throw new EntityNotFoundException("No se encontró el cliente con ID: " + id);
		}
		clienteRepo.deleteById(id);
	}

	@Override
	public List<ClienteResponseDTO> buscarPorDniOCuit(String documento) {
		List<ClienteResponseDTO> resultado = new ArrayList<>();
		for (Cliente c : clienteRepo.findAll()) {
			if (c.getId() != null && c.getId().equals(documento)) {
				resultado.add(clienteMapper.toResponseDTO(c));
			} else if (c instanceof Particular p && p.getDni() != null && p.getDni().equals(documento)) {
				resultado.add(clienteMapper.toResponseDTO(c));
			}
		}
		return resultado;
	}

	@Override
	public List<ClienteResponseDTO> buscarPorNombreORazonSocial(String nombre) {
		List<ClienteResponseDTO> resultado = new ArrayList<>();
		String filtro = nombre.toLowerCase();

		for (Cliente c : clienteRepo.findAll()) {
			if (c instanceof Particular p) {
				if ((p.getNombre() != null && p.getNombre().toLowerCase().contains(filtro)) ||
					(p.getApellido() != null && p.getApellido().toLowerCase().contains(filtro))) {
					resultado.add(clienteMapper.toResponseDTO(p));
				}
			} else if (c instanceof Empresa e) {
				if (e.getRazonSocial() != null && e.getRazonSocial().toLowerCase().contains(filtro)) {
					resultado.add(clienteMapper.toResponseDTO(e));
				}
			}
		}
		return resultado;
	}

	@Override
	public EstadoCuentaDTO consultarEstadoCuenta(String idCliente) {
		Cliente cliente = clienteRepo.findById(idCliente)
				.orElseThrow(() -> new EntityNotFoundException("No se encontró el cliente en el sistema"));

		List<Venta> ventasDelCliente = ventaRepo.findByClienteId(idCliente);
		if (ventasDelCliente != null) {
			for (Venta v : ventasDelCliente) {
				if (v instanceof Financiada ventaFinanciada) {
					if (ventaFinanciada.getCuotas() != null) {
						for (Cuota c : ventaFinanciada.getCuotas()) {
							c.verificarVencimiento();
						}
					}
					ventaFinanciada.actualizarEstado();
				}
			}
		}

		return clienteMapper.toEstadoCuentaDTO(cliente, ventasDelCliente);
	}
}