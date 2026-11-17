package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class VentaEstadoCuentaDTO {
	private Integer idVenta;
	private LocalDate fechaVenta;
	private String estadoGeneral;
	private List<RespuestaCuotaDTO> cuotas; 
}