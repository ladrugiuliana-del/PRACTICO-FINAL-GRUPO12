package ar.edu.unju.fi.poo.controller;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.exceptions.ProductoException; // Asumimos que el Integrante 4 la creó acá
import ar.edu.unju.fi.poo.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    // ENDPOINTS DE CONSULTA (Filtros del Travalo)

    @GetMapping
    public List<ProductoDto> listar() {
        return service.listar();
    }

    @GetMapping("/filtrar")
    public List<ProductoDto> filtrarProductos(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax) {
        return service.filtrarProductos(categoriaId, marca, precioMin, precioMax);
    }

    @GetMapping("/stock-critico")
    public List<ProductoDto> consultarStockCritico(
            @RequestParam(defaultValue = "10") Integer umbral) {
        return service.buscarPorStockCritico(umbral);
    }

    // ENDPOINTS CRUD

    @GetMapping("/{id}")
    public ProductoDto buscar(@PathVariable Integer id) {
        try {
            return service.buscarPorIdDto(id);
        } catch (ProductoException e) {
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crear(@RequestBody ProductoDto producto) {
        try {
            ProductoDto nuevo = service.crear(producto);
            return ResponseEntity.created(URI.create("/api/productos/" + nuevo.getId())).body(nuevo);
        } catch (ProductoException e) {
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ProductoDto actualizar(@PathVariable Integer id, @RequestBody ProductoDto producto) {
        try {
            return service.actualizar(id, producto);
        } catch (ProductoException e) {
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        try {
            service.eliminar(id);
        } catch (ProductoException e) {
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }
}