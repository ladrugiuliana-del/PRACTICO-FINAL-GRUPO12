package ar.edu.unju.fi.poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de entrada para registrar un nuevo cliente (PARTICULAR o EMPRESA)")
public class ClienteRequestDTO {

    @Schema(
        description = "Tipo de cliente a registrar",
        example = "PARTICULAR",
        allowableValues = {"PARTICULAR", "EMPRESA"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoCliente;

    @Schema(
        description = "Correo electrónico único del cliente",
        example = "juan.perez@email.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(description = "Número de teléfono de contacto", example = "3888-123456")
    private String telefono;

    @Schema(description = "Domicilio del cliente", example = "Av. Libertad 450, San Pedro de Jujuy")
    private String direccion;

    @Schema(description = "DNI del cliente (obligatorio para PARTICULAR; se utiliza como ID)", example = "40123456")
    private String dni;

    @Schema(description = "Nombre del cliente (obligatorio para PARTICULAR)", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del cliente (obligatorio para PARTICULAR)", example = "Pérez")
    private String apellido;

    @Schema(description = "CUIT de la empresa (obligatorio para EMPRESA; se utiliza como ID)", example = "30-71234567-9")
    private String cuit;

    @Schema(description = "Razón social de la empresa (obligatorio para EMPRESA)", example = "Distribuidora Norte S.A.")
    private String razonSocial;
}