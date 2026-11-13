package ar.edu.unju.fi.poo.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.unju.fi.poo.dto.VentaRequestDTO;
import ar.edu.unju.fi.poo.model.Contado;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.Financiada;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.service.VentaService;

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
public class VentaController {

	@Autowired
	private VentaService service;

	//Registra una venta al contado
	@PostMapping("/contado")
	public ResponseEntity<Contado> registrarContado(@RequestBody VentaRequestDTO dto) {
		try {
			// Nota: Aquí adaptas los datos del DTO a lo que requiere la firma del servicio de tus compañeros
			Contado nuevaVenta = service.realizarVentaContado(
					dto.getClienteId(), 
					null, // detalles mapeados desde el DTO
					dto.getCodigoCupon()
			);
			return ResponseEntity.created(URI.create("/api/v1/ventas/contado/" + nuevaVenta.getId())).body(nuevaVenta);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	//Registra una venta financiada
	@PostMapping("/financiada")
	public ResponseEntity<Financiada> registrarFinanciada(@RequestBody VentaRequestDTO dto) {
		try {
			Financiada nuevaVenta = service.realizarVentaFinanciada(
					dto.getClienteId(), 
					null, // detalles mapeados
					15 // porcentaje de recargo de ejemplo
			);
			return ResponseEntity.created(URI.create("/api/v1/ventas/financiada/" + nuevaVenta.getId())).body(nuevaVenta);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	//Consulta el historial de ventas asociadas a un cliente en particular.
	@GetMapping("/cliente/{clienteId}")
	public List<Venta> buscarHistorialPorCliente(@PathVariable String clienteId) {
		try {
			return service.buscarHistorialPorCliente(clienteId);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	//Consulta el estado de cuenta de las cuotas pendientes de pago de un cliente
	@GetMapping("/cliente/{clienteId}/cuotas-pendientes")
	public List<Cuota> consultarCuotasPendientes(@PathVariable String clienteId) {
		try {
			return service.consultarCuotasPendientesPorCliente(clienteId);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	//Filtra las ventas realizadas dentro de un período o rango de fechas específico.
	@GetMapping("/filtro-fechas")
	public List<Venta> buscarVentasPorFecha(
			@RequestParam("inicio") LocalDate inicio, 
			@RequestParam("fin") LocalDate fin) {
		try {
			return service.buscarVentasPorFecha(inicio, fin);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	//Lista todas las ventas de modalidad contado.
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