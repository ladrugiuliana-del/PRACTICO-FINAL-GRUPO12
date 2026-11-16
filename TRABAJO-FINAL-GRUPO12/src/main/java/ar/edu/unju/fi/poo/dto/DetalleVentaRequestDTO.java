package ar.edu.unju.fi.poo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequestDTO {
    private Integer productoId;
    private Integer cantidad;
}