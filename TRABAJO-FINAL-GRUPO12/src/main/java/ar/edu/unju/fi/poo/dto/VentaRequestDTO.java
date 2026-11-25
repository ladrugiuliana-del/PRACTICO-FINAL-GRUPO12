package ar.edu.unju.fi.poo.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de entrada necesarios para registrar una venta (CONTADO o FINANCIADA)")
public class VentaRequestDTO {

    @Schema(
        description = "Identificador único del cliente que realiza la compra (DNI o CUIT)",
        example = "40123456",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String clienteId;

    @Schema(
        description = "Modalidad de pago de la venta",
        example = "CONTADO",
        allowableValues = {"CONTADO", "FINANCIADA"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoPago;

    @Schema(
        description = "Código de cupón de descuento aplicable (opcional, solo para modalidad CONTADO)",
        example = "DESC10",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String codigoCupon;

    @Schema(
        description = "Listado de productos y cantidades incluidos en la venta",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<DetalleVentaRequestDTO> detalles;
}