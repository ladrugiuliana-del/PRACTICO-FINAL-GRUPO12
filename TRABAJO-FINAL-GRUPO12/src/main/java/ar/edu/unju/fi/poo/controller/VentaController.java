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
import ar.edu.unju.fi.poo.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas y Cuotas", description = "Endpoints para registro de ventas, historial y cobro correlativo de cuotas")
public class VentaController {

	private final VentaService service;

	@Operation(summary = "Registrar venta", description = "Crea una nueva venta al contado o financiada según el tipo de pago.")
	@PostMapping
	public ResponseEntity<RespuestaVentaHistorialDTO> registrarVenta(@RequestBody VentaRequestDTO dto) {
		RespuestaVentaHistorialDTO nuevaVenta = service.registrarVenta(dto);
		return ResponseEntity.created(URI.create("/api/v1/ventas/" + nuevaVenta.getId())).body(nuevaVenta);
	}

	@Operation(
		summary = "Cobrar cuota correlativa de una venta",
		description = "Impacta el pago sobre la primera cuota pendiente de la venta sin permitir saltear cuotas anteriores. Si se abona la última cuota, la venta pasa a estado SALDADA."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Cuota cobrada exitosamente"),
		@ApiResponse(responseCode = "404", description = "Venta no encontrada"),
		@ApiResponse(responseCode = "422", description = "Regla de negocio violada (venta ya saldada o intento de saltear cuotas)")
	})
	@PostMapping("/financiada/{ventaId}/cobrar-cuota")
	public ResponseEntity<RespuestaCuotaDTO> cobrarCuota(
			@Parameter(description = "Identificador de la venta financiada") @PathVariable String ventaId) {
		return ResponseEntity.ok(service.cobrarSiguienteCuota(ventaId));
	}

	@Operation(summary = "Consultar historial de ventas por cliente", description = "Obtiene todas las ventas registradas para un cliente específico.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Historial obtenido correctamente"),
		@ApiResponse(responseCode = "404", description = "Cliente no encontrado")
	})
	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> buscarHistorialPorCliente(
			@Parameter(description = "ID del cliente") @PathVariable String clienteId) {
		return ResponseEntity.ok(service.buscarHistorialPorCliente(clienteId));
	}

	@Operation(summary = "Consultar cuotas pendientes o vencidas de un cliente", description = "Devuelve el detalle de las cuotas aún no abonadas del cliente.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de cuotas recuperado correctamente"),
		@ApiResponse(responseCode = "404", description = "Cliente no encontrado")
	})
	@GetMapping("/cliente/{clienteId}/cuotas-pendientes")
	public ResponseEntity<List<RespuestaCuotaDTO>> consultarCuotasPendientes(
			@Parameter(description = "ID del cliente") @PathVariable String clienteId) {
		return ResponseEntity.ok(service.consultarCuotasPendientesPorCliente(clienteId));
	}

	@Operation(summary = "Filtrar ventas por rango de fechas", description = "Lista las ventas realizadas entre la fecha de inicio y fin indicadas.")
	@GetMapping("/filtro-fechas")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> buscarVentasPorFecha(
			@RequestParam("inicio") LocalDate inicio,
			@RequestParam("fin") LocalDate fin) {
		return ResponseEntity.ok(service.buscarVentasPorFecha(inicio, fin));
	}

	@Operation(summary = "Listar ventas al contado", description = "Recupera todas las ventas registradas bajo modalidad contado.")
	@GetMapping("/contado")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> listarContado() {
		return ResponseEntity.ok(service.buscarVentasContado());
	}

	@Operation(summary = "Listar ventas financiadas", description = "Recupera todas las ventas registradas bajo modalidad financiada.")
	@GetMapping("/financiadas")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> listarFinanciadas() {
		return ResponseEntity.ok(service.buscarVentasFinanciadas());
	}
}