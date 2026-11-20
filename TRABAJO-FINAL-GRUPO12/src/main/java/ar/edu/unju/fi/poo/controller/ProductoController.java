package ar.edu.unju.fi.poo.controller;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Operaciones CRUD y filtros de productos y stock")
public class ProductoController {

    private final ProductoService service;

    @Operation(summary = "Listar todos los productos")
    @GetMapping
    public List<ProductoDto> listar() {
        return service.listar();
    }

    @Operation(summary = "Filtrar productos por categoría, marca o rango de precios")
    @GetMapping("/filtrar")
    public List<ProductoDto> filtrarProductos(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax) {
        return service.filtrarProductos(categoriaId, marca, precioMin, precioMax);
    }

    @Operation(summary = "Consultar productos con stock crítico")
    @GetMapping("/stock-critico")
    public List<ProductoDto> consultarStockCritico(
            @RequestParam(defaultValue = "10") Integer umbral) {
        return service.buscarPorStockCritico(umbral);
    }

    @Operation(summary = "Buscar producto por ID")
    @GetMapping("/{id}")
    public ProductoDto buscar(@PathVariable Integer id) {
        return service.buscarPorIdDto(id);
    }

    @Operation(summary = "Crear un nuevo producto")
    @PostMapping
    public ResponseEntity<ProductoDto> crear(@RequestBody ProductoDto producto) {
        ProductoDto nuevo = service.crear(producto);
        return ResponseEntity.created(URI.create("/api/productos/" + nuevo.getId())).body(nuevo);
    }

    @Operation(summary = "Actualizar un producto existente")
    @PutMapping("/{id}")
    public ProductoDto actualizar(@PathVariable Integer id, @RequestBody ProductoDto producto) {
        return service.actualizar(id, producto);
    }

    @Operation(summary = "Eliminar un producto por ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}