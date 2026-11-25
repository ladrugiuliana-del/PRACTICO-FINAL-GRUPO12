package ar.edu.unju.fi.poo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.dto.EstadoCuentaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaErrorDTO;
import ar.edu.unju.fi.poo.service.ClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes (particulares y empresas) y consulta de estado de cuenta")
public class ClienteController {

	private final ClienteService clienteService;

	@Operation(
		summary = "Registrar un nuevo cliente",
		description = "Crea un cliente de tipo PARTICULAR (con DNI, nombre y apellido) o EMPRESA (con CUIT y razón social), validando que no exista un email o DNI duplicado.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Datos del cliente a registrar",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ClienteRequestDTO.class),
				examples = {
					@ExampleObject(
						name = "Cliente Particular",
						summary = "Ejemplo de alta de persona particular",
						value = """
						{
						  "tipoCliente": "PARTICULAR",
						  "email": "juan.perez@email.com",
						  "telefono": "3888-123456",
						  "direccion": "Av. Libertad 450, San Pedro de Jujuy",
						  "dni": "40123456",
						  "nombre": "Juan",
						  "apellido": "Pérez"
						}
						"""
					),
					@ExampleObject(
						name = "Cliente Empresa",
						summary = "Ejemplo de alta de cliente empresa",
						value = """
						{
						  "tipoCliente": "EMPRESA",
						  "email": "contacto@distribuidoranorte.com",
						  "telefono": "3888-654321",
						  "direccion": "Ruta 34 Km 1190, San Pedro de Jujuy",
						  "cuit": "30-71234567-9",
						  "razonSocial": "Distribuidora Norte S.A."
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
			description = "Cliente creado exitosamente",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Datos o tipo de cliente inválidos",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		),
		@ApiResponse(
			responseCode = "409",
			description = "El email o DNI ya se encuentra registrado en el sistema",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@PostMapping
	public ResponseEntity<ClienteResponseDTO> crearCliente(@RequestBody ClienteRequestDTO dto) {
		ClienteResponseDTO respuesta = clienteService.registrarCliente(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@Operation(summary = "Listar todos los clientes", description = "Devuelve el listado completo de clientes registrados.")
	@ApiResponse(
		responseCode = "200",
		description = "Listado de clientes recuperado correctamente",
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClienteResponseDTO.class)))
	)
	@GetMapping
	public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
		return ResponseEntity.ok(clienteService.obtenerTodosLosClientes());
	}

	@Operation(summary = "Buscar cliente por ID", description = "Recupera los datos de un cliente específico mediante su identificador (DNI o CUIT).")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Cliente encontrado",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "No existe un cliente con el ID indicado",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> buscarPorId(
			@Parameter(description = "Identificador único del cliente (DNI o CUIT)", example = "40123456", required = true)
			@PathVariable String id) {
		return ResponseEntity.ok(clienteService.buscarPorId(id));
	}

	@Operation(summary = "Eliminar un cliente", description = "Elimina un cliente del sistema según su identificador.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
		@ApiResponse(
			responseCode = "404",
			description = "No existe un cliente con el ID indicado",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCliente(
			@Parameter(description = "Identificador único del cliente a eliminar (DNI o CUIT)", example = "40123456", required = true)
			@PathVariable String id) {
		clienteService.eliminarCliente(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "Consultar Estado de Cuenta",
		description = "Recupera la información detallada de un cliente, su historial de ventas registradas y el estado actualizado de sus cuotas (ADEUDADA, VENCIDA o PAGADA)."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Estado de cuenta recuperado correctamente",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstadoCuentaDTO.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "No existe un cliente con el ID proporcionado",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
		)
	})
	@GetMapping("/{id}/estado-cuenta")
	public ResponseEntity<EstadoCuentaDTO> consultarEstadoCuenta(
			@Parameter(description = "Identificador único del cliente (DNI o CUIT)", example = "40123456", required = true)
			@PathVariable String id) {
		return ResponseEntity.ok(clienteService.consultarEstadoCuenta(id));
	}
}