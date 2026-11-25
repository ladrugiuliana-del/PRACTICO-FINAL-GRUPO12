package ar.edu.unju.fi.poo.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Estado de cuenta completo de un cliente, incluyendo su historial de ventas y detalle de cuotas")
public class EstadoCuentaDTO {

    @Schema(description = "Identificador único del cliente (DNI o CUIT)", example = "40123456")
    private String idCliente;

    @Schema(description = "Nombre completo o Razón Social del cliente", example = "Juan Pérez")
    private String nombreORazonSocial;

    @Schema(description = "Correo electrónico registrado", example = "juan.perez@email.com")
    private String email;

    @Schema(description = "Historial de ventas registradas con el estado actualizado de sus cuotas")
    private List<VentaEstadoCuentaDTO> historialVentas;
}