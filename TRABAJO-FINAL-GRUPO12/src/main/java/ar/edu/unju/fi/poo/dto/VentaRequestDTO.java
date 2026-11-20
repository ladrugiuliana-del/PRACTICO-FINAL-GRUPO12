package ar.edu.unju.fi.poo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {
    private String clienteId; // tipo de identificador que tenga Cliente
    private String tipoPago;   // "CONTADO" o "FINANCIADA"
    private String codigoCupon; // Opcional para contado
    private List<DetalleVentaRequestDTO> detalles;
}