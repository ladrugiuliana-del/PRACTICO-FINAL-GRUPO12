package ar.edu.unju.fi.poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ítem de producto y cantidad solicitada dentro de una venta")
public class DetalleVentaRequestDTO {

    @Schema(
        description = "Identificador único del producto a comprar",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer productoId;

    @Schema(
        description = "Cantidad de unidades del producto",
        example = "2",
        minimum = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;
}