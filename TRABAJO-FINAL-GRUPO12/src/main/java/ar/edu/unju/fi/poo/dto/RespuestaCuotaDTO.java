package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información detallada de una cuota perteneciente a una venta financiada")
public class RespuestaCuotaDTO {

    @Schema(description = "Identificador único de la cuota", example = "1")
    private Integer id;

    @Schema(description = "Número correlativo de la cuota dentro del plan de pagos", example = "1")
    private Integer nCuota;

    @Schema(description = "Monto correspondiente a la cuota", example = "28750.0")
    private double importe;

    @Schema(description = "Fecha límite de vencimiento de la cuota", example = "2026-12-24")
    private LocalDate fechaVencimiento;

    @Schema(
        description = "Estado actual de la cuota",
        example = "ADEUDADA",
        allowableValues = {"ADEUDADA", "VENCIDA", "PAGADA"}
    )
    private String estadoCuota;

    @Schema(description = "Código de comprobante de la venta financiada asociada", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private String codigoComprobanteVenta;
}