package ar.edu.unju.fi.poo.dto;

import java.util.List;
import lombok.Data;

@Data
public class EstadoCuentaDTO {
	private String idCliente;
	private String nombreORazonSocial;
	private String email;
	private List<VentaEstadoCuentaDTO> historialVentas;
}