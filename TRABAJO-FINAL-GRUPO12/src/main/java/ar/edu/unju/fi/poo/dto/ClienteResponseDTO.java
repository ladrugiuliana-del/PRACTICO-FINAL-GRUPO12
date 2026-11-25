package ar.edu.unju.fi.poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos públicos resumidos de un cliente registrado")
public class ClienteResponseDTO {

    @Schema(description = "Identificador único del cliente (DNI para PARTICULAR o CUIT para EMPRESA)", example = "40123456")
    private String id;

    @Schema(
        description = "Tipo de cliente registrado",
        example = "PARTICULAR",
        allowableValues = {"PARTICULAR", "EMPRESA"}
    )
    private String tipoCliente;

    @Schema(description = "Documento identificatorio (DNI o CUIT)", example = "40123456")
    private String identificacion;

    @Schema(description = "Nombre completo o Razón Social del cliente", example = "Juan Pérez")
    private String nombreORazonSocial;

    @Schema(description = "Correo electrónico de contacto", example = "juan.perez@email.com")
    private String email;
}