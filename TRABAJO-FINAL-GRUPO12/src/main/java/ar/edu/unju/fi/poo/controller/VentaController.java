package ar.edu.unju.fi.poo.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaVentaHistorialDTO;
import ar.edu.unju.fi.poo.dto.VentaRequestDTO;
import ar.edu.unju.fi.poo.entity.Contado;
import ar.edu.unju.fi.poo.entity.Financiada;
import ar.edu.unju.fi.poo.entity.Venta;
import ar.edu.unju.fi.poo.service.ClienteService;
import ar.edu.unju.fi.poo.service.VentaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de ventas.
 *
 * Expone los endpoints de la versión 1 de la API y delega la lógica de negocio
 * a VentaService, aplicando manejo centralizado de excepciones y DTOs.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

	private final VentaService service;
	private final ClienteService clienteService;

	// Registra una venta al contado
	@PostMapping("/contado")
	public ResponseEntity<Contado> registrarContado(@RequestBody VentaRequestDTO dto) {
		Contado nuevaVenta = service.realizarVentaContado(
				dto.getClienteId(), 
				null, // detalles mapeados desde el DTO
				dto.getCodigoCupon()
		);
		return ResponseEntity.created(URI.create("/api/v1/ventas/contado/" + nuevaVenta.getId())).body(nuevaVenta);
	}

	// Registra una venta financiada
	@PostMapping("/financiada")
	public ResponseEntity<Financiada> registrarFinanciada(@RequestBody VentaRequestDTO dto) {
		Financiada nuevaVenta = service.realizarVentaFinanciada(
				dto.getClienteId(), 
				null, // detalles mapeados
				15 // porcentaje de recargo de ejemplo
		);
		return ResponseEntity.created(URI.create("/api/v1/ventas/financiada/" + nuevaVenta.getId())).body(nuevaVenta);
	}

	// Consulta el historial de ventas asociadas a un cliente en particular (con tus DTOs)
	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> buscarHistorialPorCliente(@PathVariable String clienteId) {
		if (clienteService.buscarPorId(clienteId) == null) {
			throw new EntityNotFoundException("No se encontró el cliente con ID: " + clienteId);
		}
		List<RespuestaVentaHistorialDTO> respuesta = service.buscarHistorialPorCliente(clienteId).stream()
				.map(v -> new RespuestaVentaHistorialDTO(
						v.getId(),
						v.getCodigoComprobante(),
						v.getFecha(),
						(v instanceof Contado) ? "CONTADO" : "FINANCIADA",
						v.getSubtotal(),
						v.getTotal(),
						(v.getDetalles() != null) ? v.getDetalles().size() : 0
				)).toList();
		return ResponseEntity.ok(respuesta);
	}

	// Consulta el estado de cuenta de las cuotas pendientes de pago de un cliente (con tus DTOs)
	@GetMapping("/cliente/{clienteId}/cuotas-pendientes")
	public ResponseEntity<List<RespuestaCuotaDTO>> consultarCuotasPendientes(@PathVariable String clienteId) {
		if (clienteService.buscarPorId(clienteId) == null) {
			throw new EntityNotFoundException("No se encontró el cliente con ID: " + clienteId);
		}
		List<RespuestaCuotaDTO> respuesta = service.consultarCuotasPendientesPorCliente(clienteId).stream()
				.map(c -> new RespuestaCuotaDTO(
						c.getId(),
						c.getNCuota(),
						c.getImporte(),
						c.getFechaVencimiento(),
						c.getEstadoCuota(),
						(c.getFinanciada() != null) ? c.getFinanciada().getCodigoComprobante() : null
				)).toList();
		return ResponseEntity.ok(respuesta);
	}

	// Filtra las ventas realizadas dentro de un período o rango de fechas específico
	@GetMapping("/filtro-fechas")
	public List<Venta> buscarVentasPorFecha(
			@RequestParam("inicio") LocalDate inicio, 
			@RequestParam("fin") LocalDate fin) {
		return service.buscarVentasPorFecha(inicio, fin);
	}

	// Lista todas las ventas de modalidad contado.
	@GetMapping("/contado")
	public List<Contado> listarContado() {
		return service.buscarVentasContado();
	}

	// Lista todas las ventas de modalidad financiada.
	@GetMapping("/financiadas")
	public List<Financiada> listarFinanciadas() {
		return service.buscarVentasFinanciadas();
	}
}