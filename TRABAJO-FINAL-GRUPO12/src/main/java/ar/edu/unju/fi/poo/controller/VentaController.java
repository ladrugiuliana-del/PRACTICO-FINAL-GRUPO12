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
import ar.edu.unju.fi.poo.dto.RespuestaErrorDTO;
import ar.edu.unju.fi.poo.dto.RespuestaVentaHistorialDTO;
import ar.edu.unju.fi.poo.dto.VentaRequestDTO;
import ar.edu.unju.fi.poo.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas y Cuotas", description = "Registro de ventas (contado y financiada), consultas de historial y cobro correlativo de cuotas")
public class VentaController {

	private final VentaService service;

	@Operation(
		summary = "Registrar una nueva venta (Contado o Financiada)",
		description = "Registra una venta validando la existencia del cliente y el stock de cada producto. "
				+ "Si tipoPago es CONTADO aplica opcionalmente un cupón de descuento; "
				+ "si tipoPago es FINANCIADA aplica un 15% de recargo y genera automáticamente el plan de 6 cuotas.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Datos de la venta y listado de productos a facturar",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = VentaRequestDTO.class),
				examples = {
					@ExampleObject(
						name = "Venta al Contado (con cupón)",
						summary = "Ejemplo de venta al contado aplicando cupón de descuento",
						value = """
						{
						  "clienteId": "40123456",
						  "tipoPago": "CONTADO",
						  "codigoCupon": "DESC10",
						  "detalles": [
						    {
						      "productoId": 1,
						      "cantidad": 2
						    }
						  ]
						}
						"""
					),
					@ExampleObject(
						name = "Venta Financiada (6 cuotas)",
						summary = "Ejemplo de venta financiada con recargo del 15% y plan de 6 cuotas",
						value = """
						{
						  "clienteId": "40123456",
						  "tipoPago": "FINANCIADA",
						  "codigoCupon": null,
						  "detalles": [
						    {
						      "productoId": 1,
						      "cantidad": 1
						    }
						  ]
						}
						"""
					)
				}
			)
		)
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Venta registrada exitosamente",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaVentaHistorialDTO.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Petición inválida (clienteId vacío, tipoPago distinto de CONTADO/FINANCIADA, detalles vacíos o cantidad <= 0)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = RespuestaErrorDTO.class),
				examples = @ExampleObject(value = """
				{
				  "timestamp": "2026-11-24T23:30:00",
				  "status": 400,
				  "error": "Petición Inválida",
				  "mensaje": "Tipo de pago no válido. Use CONTADO o FINANCIADA.",
				  "ruta": "/api/v1/ventas"
				}
				""")
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "Cliente o producto inexistente",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		),
		@ApiResponse(
			responseCode = "422",
			description = "Stock insuficiente para alguno de los productos solicitados",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = RespuestaErrorDTO.class),
				examples = @ExampleObject(value = """
				{
				  "timestamp": "2026-11-24T23:30:00",
				  "status": 422,
				  "error": "Error de Regla de Negocio",
				  "mensaje": "Stock insuficiente para el producto: Notebook Lenovo IdeaPad 3",
				  "ruta": "/api/v1/ventas"
				}
				""")
			)
		)
	})
	@PostMapping
	public ResponseEntity<RespuestaVentaHistorialDTO> registrarVenta(@RequestBody VentaRequestDTO dto) {
		RespuestaVentaHistorialDTO nuevaVenta = service.registrarVenta(dto);
		return ResponseEntity.created(URI.create("/api/v1/ventas/" + nuevaVenta.getId())).body(nuevaVenta);
	}

	@Operation(
		summary = "Cobrar siguiente cuota correlativa de una venta",
		description = "Impacta el pago sobre la primera cuota impaga (ADEUDADA o VENCIDA) en orden correlativo estricto. "
				+ "Actualiza el vencimiento si corresponde y marca la venta como SALDADA al abonarse la última cuota."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Cuota cobrada y actualizada a estado PAGADA",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaCuotaDTO.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "El identificador de la venta no es numérico",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "No existe una venta con el ID indicado",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		),
		@ApiResponse(
			responseCode = "422",
			description = "Operación rechazada (venta al contado, venta ya SALDADA o intento de saltear cuotas anteriores impagas)",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@PostMapping("/financiada/{ventaId}/cobrar-cuota")
	public ResponseEntity<RespuestaCuotaDTO> cobrarCuota(
			@Parameter(description = "ID numérico de la venta financiada", example = "1", required = true)
			@PathVariable String ventaId) {
		return ResponseEntity.ok(service.cobrarSiguienteCuota(ventaId));
	}

	@Operation(
		summary = "Consultar historial de ventas de un cliente",
		description = "Recupera el listado completo de ventas registradas (contado y financiadas) asociadas al cliente indicado."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Historial de ventas recuperado correctamente",
			content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RespuestaVentaHistorialDTO.class)))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Cliente no encontrado",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> buscarHistorialPorCliente(
			@Parameter(description = "Identificador único del cliente (DNI o CUIT)", example = "40123456", required = true)
			@PathVariable String clienteId) {
		return ResponseEntity.ok(service.buscarHistorialPorCliente(clienteId));
	}

	@Operation(
		summary = "Consultar cuotas pendientes o vencidas de un cliente",
		description = "Devuelve las cuotas en estado ADEUDADA o VENCIDA ordenadas correlativamente, actualizando automáticamente las que superaron su fecha de vencimiento."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Listado de cuotas impagas recuperado correctamente",
			content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RespuestaCuotaDTO.class)))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Cliente no encontrado",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@GetMapping("/cliente/{clienteId}/cuotas-pendientes")
	public ResponseEntity<List<RespuestaCuotaDTO>> consultarCuotasPendientes(
			@Parameter(description = "Identificador único del cliente (DNI o CUIT)", example = "40123456", required = true)
			@PathVariable String clienteId) {
		return ResponseEntity.ok(service.consultarCuotasPendientesPorCliente(clienteId));
	}

	@Operation(
		summary = "Filtrar ventas por rango de fechas",
		description = "Devuelve todas las ventas registradas entre la fecha de inicio y la fecha de fin (inclusive)."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Listado de ventas dentro del período indicado",
			content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RespuestaVentaHistorialDTO.class)))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Formato de fecha inválido (se espera YYYY-MM-DD)",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@GetMapping("/filtro-fechas")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> buscarVentasPorFecha(
			@Parameter(description = "Fecha de inicio del rango (formato ISO YYYY-MM-DD)", example = "2026-01-01", required = true)
			@RequestParam("inicio") LocalDate inicio,
			@Parameter(description = "Fecha de fin del rango (formato ISO YYYY-MM-DD)", example = "2026-12-31", required = true)
			@RequestParam("fin") LocalDate fin) {
		return ResponseEntity.ok(service.buscarVentasPorFecha(inicio, fin));
	}

	@Operation(summary = "Listar ventas al contado", description = "Recupera todas las ventas registradas bajo la modalidad CONTADO.")
	@ApiResponse(
		responseCode = "200",
		description = "Listado de ventas al contado",
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RespuestaVentaHistorialDTO.class)))
	)
	@GetMapping("/contado")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> listarContado() {
		return ResponseEntity.ok(service.buscarVentasContado());
	}

	@Operation(summary = "Listar ventas financiadas", description = "Recupera todas las ventas registradas bajo la modalidad FINANCIADA.")
	@ApiResponse(
		responseCode = "200",
		description = "Listado de ventas financiadas",
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RespuestaVentaHistorialDTO.class)))
	)
	@GetMapping("/financiadas")
	public ResponseEntity<List<RespuestaVentaHistorialDTO>> listarFinanciadas() {
		return ResponseEntity.ok(service.buscarVentasFinanciadas());
	}
}