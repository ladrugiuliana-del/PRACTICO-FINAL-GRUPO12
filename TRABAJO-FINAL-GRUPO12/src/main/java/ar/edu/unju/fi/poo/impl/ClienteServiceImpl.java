package ar.edu.unju.fi.poo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.dto.EstadoCuentaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.VentaEstadoCuentaDTO;
import ar.edu.unju.fi.poo.entity.Cliente;
import ar.edu.unju.fi.poo.entity.Cuota;
import ar.edu.unju.fi.poo.entity.Empresa;
import ar.edu.unju.fi.poo.entity.Financiada;
import ar.edu.unju.fi.poo.entity.Particular;
import ar.edu.unju.fi.poo.entity.Venta;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
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

	@Override
	public ClienteResponseDTO registrarCliente(ClienteRequestDTO dto) {
		Cliente cliente = mapearAEntidad(dto);
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
		return mapearADTO(guardado);
	}

	@Override
	public List<ClienteResponseDTO> obtenerTodosLosClientes() {
		List<ClienteResponseDTO> respuesta = new ArrayList<>();
		for (Cliente c : clienteRepo.findAll()) {
			respuesta.add(mapearADTO(c));
		}
		return respuesta;
	}

	@Override
	public ClienteResponseDTO buscarPorId(String id) {
		Cliente cliente = clienteRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("No se encontró el cliente con ID: " + id));
		return mapearADTO(cliente);
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
				resultado.add(mapearADTO(c));
			} else if (c instanceof Particular p && p.getDni() != null && p.getDni().equals(documento)) {
				resultado.add(mapearADTO(c));
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
					resultado.add(mapearADTO(p));
				}
			} else if (c instanceof Empresa e) {
				if (e.getRazonSocial() != null && e.getRazonSocial().toLowerCase().contains(filtro)) {
					resultado.add(mapearADTO(e));
				}
			}
		}
		return resultado;
	}

	private ClienteResponseDTO mapearADTO(Cliente cliente) {
		ClienteResponseDTO dto = new ClienteResponseDTO();
		dto.setId(cliente.getId());
		dto.setEmail(cliente.getEmail());

		if (cliente instanceof Particular p) {
			dto.setTipoCliente("PARTICULAR");
			dto.setIdentificacion(p.getDni());
			dto.setNombreORazonSocial(p.getNombre() + " " + p.getApellido());
		} else if (cliente instanceof Empresa e) {
			dto.setTipoCliente("EMPRESA");
			dto.setIdentificacion("Sin CUIT registrado");
			dto.setNombreORazonSocial(e.getRazonSocial());
		}

		return dto;
	}

	private Cliente mapearAEntidad(ClienteRequestDTO dto) {
		if (dto == null || dto.getTipoCliente() == null) {
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
		Cliente cliente = clienteRepo.findById(idCliente)
				.orElseThrow(() -> new EntityNotFoundException("No se encontró el cliente en el sistema"));

		EstadoCuentaDTO estado = new EstadoCuentaDTO();
		estado.setIdCliente(cliente.getId());
		estado.setEmail(cliente.getEmail());

		if (cliente instanceof Particular p) {
			estado.setNombreORazonSocial(p.getNombre() + " " + p.getApellido());
		} else if (cliente instanceof Empresa e) {
			estado.setNombreORazonSocial(e.getRazonSocial());
		}

		List<Venta> ventasDelCliente = ventaRepo.findByClienteId(idCliente);
		List<VentaEstadoCuentaDTO> historial = new ArrayList<>();

		if (ventasDelCliente != null) {
			for (Venta v : ventasDelCliente) {
				VentaEstadoCuentaDTO ventaDTO = new VentaEstadoCuentaDTO();
				ventaDTO.setIdVenta(v.getId());
				ventaDTO.setFechaVenta(v.getFecha());

				if (v instanceof Financiada ventaFinanciada) {
					List<RespuestaCuotaDTO> listaCuotasDTO = new ArrayList<>();

					if (ventaFinanciada.getCuotas() != null) {
						for (Cuota c : ventaFinanciada.getCuotas()) {
							c.verificarVencimiento();
							RespuestaCuotaDTO cDTO = new RespuestaCuotaDTO();
							cDTO.setId(c.getId());
							cDTO.setNCuota(c.getNCuota());
							cDTO.setImporte(c.getImporte());
							cDTO.setFechaVencimiento(c.getFechaVencimiento());
							cDTO.setEstadoCuota(c.getEstadoCuota());
							cDTO.setCodigoComprobanteVenta(ventaFinanciada.getCodigoComprobante());
							listaCuotasDTO.add(cDTO);
						}
					}

					ventaFinanciada.actualizarEstado();
					ventaDTO.setEstadoGeneral(ventaFinanciada.getEstado());
					ventaDTO.setCuotas(listaCuotasDTO);
				} else {
					ventaDTO.setEstadoGeneral("SALDADA");
					ventaDTO.setCuotas(new ArrayList<>());
				}

				historial.add(ventaDTO);
			}
		}

		estado.setHistorialVentas(historial);
		return estado;
	}
}