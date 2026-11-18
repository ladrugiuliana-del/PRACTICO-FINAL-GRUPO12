package ar.edu.unju.fi.poo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.entity.Cliente;
import ar.edu.unju.fi.poo.entity.Empresa;
import ar.edu.unju.fi.poo.entity.Particular;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ParticularRepository;
import ar.edu.unju.fi.poo.service.ClienteService;
import lombok.RequiredArgsConstructor;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.entity.Venta;
import ar.edu.unju.fi.poo.entity.Cuota;
import ar.edu.unju.fi.poo.entity.Financiada;
import ar.edu.unju.fi.poo.dto.EstadoCuentaDTO;
import ar.edu.unju.fi.poo.dto.VentaEstadoCuentaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

	private final ClienteRepository clienteRepo;
	private final ParticularRepository particularRepo;
	private final VentaRepository ventaRepo;

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
		List<Cliente> clientes = (List<Cliente>) clienteRepo.findAll();
		
		for (Cliente c : clientes) {
			if (c.getId() != null && c.getId().equals(documento)) {
				return c;
			}
			if (c instanceof Particular) {
				Particular p = (Particular) c;
				if (p.getDni() != null && p.getDni().equals(documento)) {
					return p;
				}
			}
		}
		return null;
	}

	@Override
	public List<Cliente> buscarPorNombreORazonSocial(String nombre) {
		List<Cliente> resultado = new ArrayList<>();
		List<Cliente> clientes = (List<Cliente>) clienteRepo.findAll();
		
		for (Cliente c : clientes) {
			if (c instanceof Particular) {
				Particular p = (Particular) c;
				if ((p.getNombre() != null && p.getNombre().toLowerCase().contains(nombre.toLowerCase())) ||
					(p.getApellido() != null && p.getApellido().toLowerCase().contains(nombre.toLowerCase()))) {
					resultado.add(p);
				}
			} else if (c instanceof Empresa) {
				Empresa e = (Empresa) c;
				if (e.getRazonSocial() != null && e.getRazonSocial().toLowerCase().contains(nombre.toLowerCase())) {
					resultado.add(e);
				}
			}
		}
		return resultado;
	}

	@Override
	public ClienteResponseDTO mapearADTO(Cliente cliente) {
		ClienteResponseDTO dto = new ClienteResponseDTO();
		dto.setId(cliente.getId());
		dto.setEmail(cliente.getEmail());
		
		if (cliente instanceof Particular) {
			Particular p = (Particular) cliente;
			dto.setTipoCliente("PARTICULAR");
			dto.setIdentificacion(p.getDni());
			dto.setNombreORazonSocial(p.getNombre() + " " + p.getApellido());
		} else if (cliente instanceof Empresa) {
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
			p.setId(dto.getDni());
			p.setEmail(dto.getEmail());
			p.setTelefono(dto.getTelefono());
			p.setDireccion(dto.getDireccion());
			p.setDni(dto.getDni());
			p.setNombre(dto.getNombre());
			p.setApellido(dto.getApellido());
			return p;
		} else if (dto.getTipoCliente().equalsIgnoreCase("EMPRESA")) {
			Empresa e = new Empresa();
			e.setEmail(dto.getEmail());
			e.setTelefono(dto.getTelefono());
			e.setDireccion(dto.getDireccion());
			e.setRazonSocial(dto.getRazonSocial());
			return e;
		}
		
		return null;
	}

	@Override
	public EstadoCuentaDTO consultarEstadoCuenta(String idCliente) {
		Cliente cliente = clienteRepo.findById(idCliente).orElse(null);
		
		if (cliente == null) {
			throw new RuntimeException("No se encontró el cliente en el sistema");
		}

		EstadoCuentaDTO estado = new EstadoCuentaDTO();
		estado.setIdCliente(cliente.getId());
		estado.setEmail(cliente.getEmail());
		
		if (cliente instanceof Particular) {
			Particular p = (Particular) cliente;
			estado.setNombreORazonSocial(p.getNombre() + " " + p.getApellido());
		} else if (cliente instanceof Empresa) {
			Empresa e = (Empresa) cliente;
			estado.setNombreORazonSocial(e.getRazonSocial());
		}

		List<Venta> ventasDelCliente = ventaRepo.findByClienteId(idCliente);
		List<VentaEstadoCuentaDTO> historial = new ArrayList<>();

		if (ventasDelCliente != null) {
			for (Venta v : ventasDelCliente) {
				VentaEstadoCuentaDTO ventaDTO = new VentaEstadoCuentaDTO();
				ventaDTO.setIdVenta(v.getId());
				ventaDTO.setFechaVenta(v.getFecha());
				
				if (v instanceof Financiada) {
					Financiada ventaFinanciada = (Financiada) v;
					
					boolean tieneDeuda = false;
					List<RespuestaCuotaDTO> listaCuotasDTO = new ArrayList<>();
					
					if (ventaFinanciada.getCuotas() != null) {
						for (Cuota c : ventaFinanciada.getCuotas()) {
							RespuestaCuotaDTO cDTO = new RespuestaCuotaDTO();
							
							cDTO.setId(c.getId());
							cDTO.setNCuota(c.getNCuota()); 
							cDTO.setImporte(c.getImporte());
							cDTO.setFechaVencimiento(c.getFechaVencimiento());
							cDTO.setEstadoCuota(c.getEstadoCuota());
							
							if ("ADEUDADA".equalsIgnoreCase(c.getEstadoCuota())) {
								tieneDeuda = true;
							}
							
							listaCuotasDTO.add(cDTO);
						}
					}
					
					ventaDTO.setEstadoGeneral(tieneDeuda ? "CON DEUDA" : "PAGADA");
					ventaDTO.setCuotas(listaCuotasDTO);
					
				} else {
					ventaDTO.setEstadoGeneral("PAGADA");
					ventaDTO.setCuotas(new ArrayList<>());
				}
				
				historial.add(ventaDTO);
			}
		}
		
		estado.setHistorialVentas(historial);
		return estado;
	}
}