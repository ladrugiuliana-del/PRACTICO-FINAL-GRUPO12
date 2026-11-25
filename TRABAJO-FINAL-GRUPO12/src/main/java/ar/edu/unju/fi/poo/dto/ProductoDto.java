package ar.edu.unju.fi.poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un producto del catálogo")
public class ProductoDto {

    @Schema(
        description = "Identificador único del producto (generado automáticamente)",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Integer id;

    @Schema(description = "Nombre comercial del producto", example = "Notebook Lenovo IdeaPad 3", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "15.6 pulgadas, 16GB RAM, SSD 512GB")
    private String descripcion;

    @Schema(description = "Marca fabricante", example = "Lenovo")
    private String marca;

    @Schema(description = "ID de la categoría a la que pertenece", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer categoriaId;

    @Schema(description = "Unidades disponibles en inventario", example = "15", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;

    @Schema(description = "Precio unitario base sin descuentos ni recargos", example = "750000.0", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precioBase;
}