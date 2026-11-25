package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen de una venta registrada devuelto al crear o consultar el historial")
public class RespuestaVentaHistorialDTO {

    @Schema(description = "Identificador único de la venta", example = "1")
    private Integer id;

    @Schema(description = "Código único de comprobante generado para la operación", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private String codigoComprobante;

    @Schema(description = "Fecha en la que se registró la venta", example = "2026-11-24")
    private LocalDate fecha;

    @Schema(
        description = "Modalidad de pago aplicada a la venta",
        example = "FINANCIADA",
        allowableValues = {"CONTADO", "FINANCIADA"}
    )
    private String modalidadPago;

    @Schema(description = "Importe subtotal antes de descuentos o recargos", example = "150000.0")
    private double subtotal;

    @Schema(description = "Importe total final de la venta", example = "172500.0")
    private double total;

    @Schema(description = "Cantidad total de ítems incluidos en la venta", example = "2")
    private int cantidadItems;
}