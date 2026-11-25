package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Detalle de una venta dentro del estado de cuenta del cliente, incluyendo sus cuotas")
public class VentaEstadoCuentaDTO {

    @Schema(description = "Identificador numérico de la venta", example = "1")
    private Integer idVenta;

    @Schema(description = "Fecha en la que se registró la venta", example = "2026-11-24")
    private LocalDate fechaVenta;

    @Schema(
        description = "Estado general actual de la venta",
        example = "PENDIENTE",
        allowableValues = {"PENDIENTE", "SALDADA"}
    )
    private String estadoGeneral;

    @Schema(description = "Listado de cuotas asociadas (vacío si la venta fue al contado)")
    private List<RespuestaCuotaDTO> cuotas;
}