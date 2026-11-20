package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaVentaHistorialDTO {

    private Integer id;
    private String codigoComprobante;
    private LocalDate fecha;
    private String modalidadPago;
    private double subtotal;
    private double total;
    private int cantidadItems;
}