package ar.edu.unju.fi.poo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.dto.RespuestaErrorDTO;
import ar.edu.unju.fi.poo.service.ProductoService;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Operaciones CRUD, filtros de catálogo y control de stock crítico")
public class ProductoController {

    private final ProductoService service;

    @Operation(summary = "Listar todos los productos", description = "Devuelve el catálogo completo de productos disponibles.")
    @ApiResponse(
        responseCode = "200",
        description = "Listado de productos obtenido correctamente",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDto.class)))
    )
    @GetMapping
    public List<ProductoDto> listar() {
        return service.listar();
    }

    @Operation(
        summary = "Filtrar productos por criterios combinables",
        description = "Permite buscar productos combinando opcionalmente categoría, marca y rango de precios."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Listado de productos que cumplen con los filtros",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDto.class)))
    )
    @GetMapping("/filtrar")
    public List<ProductoDto> filtrarProductos(
            @Parameter(description = "ID de la categoría del producto", example = "1")
            @RequestParam(required = false) Integer categoriaId,
            @Parameter(description = "Nombre de la marca fabricante", example = "Lenovo")
            @RequestParam(required = false) String marca,
            @Parameter(description = "Precio base mínimo", example = "100000")
            @RequestParam(required = false) Double precioMin,
            @Parameter(description = "Precio base máximo", example = "900000")
            @RequestParam(required = false) Double precioMax) {
        return service.filtrarProductos(categoriaId, marca, precioMin, precioMax);
    }

    @Operation(
        summary = "Consultar productos con stock crítico",
        description = "Retorna los productos cuyo stock actual es menor o igual al umbral especificado (por defecto 10 unidades)."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Listado de productos con stock crítico",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDto.class)))
    )
    @GetMapping("/stock-critico")
    public List<ProductoDto> consultarStockCritico(
            @Parameter(description = "Cantidad máxima de stock para considerar crítico", example = "10")
            @RequestParam(defaultValue = "10") Integer umbral) {
        return service.buscarPorStockCritico(umbral);
    }

    @Operation(summary = "Buscar producto por ID", description = "Obtiene el detalle de un producto a partir de su identificador numérico.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
        )
    })
    @GetMapping("/{id}")
    public ProductoDto buscar(
            @Parameter(description = "Identificador numérico del producto", example = "1", required = true)
            @PathVariable Integer id) {
        return service.buscarPorIdDto(id);
    }

    @Operation(
        summary = "Crear un nuevo producto",
        description = "Registra un nuevo producto en el catálogo asignándolo a una categoría existente.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del nuevo producto",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductoDto.class),
                examples = @ExampleObject(
                    name = "Nuevo producto",
                    value = """
                    {
                      "nombre": "Notebook Lenovo IdeaPad 3",
                      "descripcion": "15.6 pulgadas, 16GB RAM, SSD 512GB",
                      "marca": "Lenovo",
                      "categoriaId": 1,
                      "stock": 15,
                      "precioBase": 750000.0
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de producto inválidos (precio negativo, stock inválido o campos faltantes)",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "La categoría indicada no existe",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
        )
    })
    @PostMapping
    public ResponseEntity<ProductoDto> crear(@RequestBody ProductoDto producto) {
        ProductoDto nuevo = service.crear(producto);
        return ResponseEntity.created(URI.create("/api/productos/" + nuevo.getId())).body(nuevo);
    }

    @Operation(
        summary = "Actualizar un producto existente",
        description = "Modifica los datos de un producto existente según su ID.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos actualizados del producto",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductoDto.class),
                examples = @ExampleObject(
                    name = "Actualización de producto",
                    value = """
                    {
                      "nombre": "Notebook Lenovo IdeaPad 3 Gen 2",
                      "descripcion": "15.6 pulgadas, 16GB RAM, SSD 1TB",
                      "marca": "Lenovo",
                      "categoriaId": 1,
                      "stock": 20,
                      "precioBase": 820000.0
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de actualización inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto o categoría no encontrados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
        )
    })
    @PutMapping("/{id}")
    public ProductoDto actualizar(
            @Parameter(description = "Identificador numérico del producto a modificar", example = "1", required = true)
            @PathVariable Integer id,
            @RequestBody ProductoDto producto) {
        return service.actualizar(id, producto);
    }

    @Operation(summary = "Eliminar un producto por ID", description = "Elimina un producto del catálogo según su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaErrorDTO.class))
        )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "Identificador numérico del producto a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        service.eliminar(id);
    }
}