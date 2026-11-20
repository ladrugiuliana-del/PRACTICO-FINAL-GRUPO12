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
import ar.edu.unju.fi.poo.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes y consulta de estado de cuenta")
public class ClienteController {

	private final ClienteService clienteService;

	@Operation(summary = "Registrar un nuevo cliente", description = "Crea un cliente en el sistema a partir de los datos enviados.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
		@ApiResponse(responseCode = "400", description = "Datos o tipo de cliente inválidos"),
		@ApiResponse(responseCode = "409", description = "DNI/CUIT o Email duplicado")
	})
	@PostMapping
	public ResponseEntity<ClienteResponseDTO> crearCliente(@RequestBody ClienteRequestDTO dto) {
		ClienteResponseDTO respuesta = clienteService.registrarCliente(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@Operation(summary = "Listar todos los clientes", description = "Devuelve el listado completo de clientes registrados.")
	@GetMapping
	public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
		return ResponseEntity.ok(clienteService.obtenerTodosLosClientes());
	}

	@Operation(summary = "Buscar cliente por ID", description = "Recupera los datos de un cliente específico mediante su identificador.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Cliente encontrado"),
		@ApiResponse(responseCode = "404", description = "Cliente no encontrado")
	})
	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> buscarPorId(
			@Parameter(description = "ID del cliente a buscar") @PathVariable String id) {
		return ResponseEntity.ok(clienteService.buscarPorId(id));
	}

	@Operation(summary = "Eliminar un cliente", description = "Elimina un cliente del sistema según su identificador.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCliente(
			@Parameter(description = "ID del cliente a eliminar") @PathVariable String id) {
		clienteService.eliminarCliente(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "Consultar Estado de Cuenta",
		description = "Recupera la información detallada de un cliente, su historial de ventas registradas y el estado de sus cuotas pendientes o vencidas."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Estado de cuenta recuperado correctamente"),
		@ApiResponse(responseCode = "404", description = "No existe un cliente con el ID proporcionado")
	})
	@GetMapping("/{id}/estado-cuenta")
	public ResponseEntity<EstadoCuentaDTO> consultarEstadoCuenta(
			@Parameter(description = "ID del cliente") @PathVariable String id) {
		return ResponseEntity.ok(clienteService.consultarEstadoCuenta(id));
	}
}