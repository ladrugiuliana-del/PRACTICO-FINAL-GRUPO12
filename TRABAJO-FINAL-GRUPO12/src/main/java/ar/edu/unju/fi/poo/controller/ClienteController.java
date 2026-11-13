package ar.edu.unju.fi.poo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.service.ClienteService;

@RestController
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	// 1. Endpoint para crear un cliente
	@PostMapping("/api/clientes")
	public ResponseEntity<ClienteResponseDTO> crearCliente(@RequestBody ClienteRequestDTO dto) {
		
		
		Cliente cliente = clienteService.mapearAEntidad(dto);
		
		if (cliente == null) {
			return ResponseEntity.badRequest().build(); // Error 400 si mandan mal el tipo
		}

		
		Cliente clienteGuardado = clienteService.registrarCliente(cliente);

		
		ClienteResponseDTO respuesta = clienteService.mapearADTO(clienteGuardado);

		// Devolvemos estado 201 
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	// 2. Endpoint para listar todos los clientes
	@GetMapping("/api/clientes")
	public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
		List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
		

		List<ClienteResponseDTO> respuesta = new ArrayList<>();
		for (Cliente c : clientes) {
			respuesta.add(clienteService.mapearADTO(c));
		}
		
		return ResponseEntity.ok(respuesta);
	}

	// 3. Endpoint para buscar un cliente por su ID
	@GetMapping("/api/clientes/{id}")
	public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable String id) {
		Cliente cliente = clienteService.buscarPorId(id);
		
		if (cliente != null) {
			return ResponseEntity.ok(clienteService.mapearADTO(cliente));
		} else {
			return ResponseEntity.notFound().build(); // Error 404 si no existe
		}
	}
	
	// 4. Endpoint para eliminar un cliente
	@DeleteMapping("/api/clientes/{id}")
	public ResponseEntity<Void> eliminarCliente(@PathVariable String id) {
		clienteService.eliminarCliente(id);
		return ResponseEntity.noContent().build();
	}
}