package ar.edu.unju.fi.poo.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Estructura estándar de respuesta ante errores de la API")
public class RespuestaErrorDTO {

    @Schema(description = "Fecha y hora exacta en la que ocurrió el error", example = "2026-11-24T23:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP devuelto", example = "404")
    private int status;

    @Schema(description = "Título o categoría del error HTTP", example = "Recurso No Encontrado")
    private String error;

    @Schema(description = "Descripción clara y detallada del motivo del error", example = "No se encontró el cliente con ID: 99999999")
    private String mensaje;

    @Schema(description = "Ruta del endpoint que originó el error", example = "/api/v1/ventas")
    private String ruta;

    public RespuestaErrorDTO(int status, String error, String mensaje, String ruta) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.ruta = ruta;
    }
}