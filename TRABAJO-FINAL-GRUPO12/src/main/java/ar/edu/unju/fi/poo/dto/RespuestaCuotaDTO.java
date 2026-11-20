package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaCuotaDTO {

    private Integer id;
    private Integer nCuota;
    private double importe;
    private LocalDate fechaVencimiento;
    private String estadoCuota;
    private String codigoComprobanteVenta;
}