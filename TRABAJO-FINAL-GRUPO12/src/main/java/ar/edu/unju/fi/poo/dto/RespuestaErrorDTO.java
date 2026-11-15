package ar.edu.unju.fi.poo.dto;

import java.time.LocalDateTime;

public class RespuestaErrorDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
    private String ruta;

    public RespuestaErrorDTO(int status, String error, String mensaje, String ruta) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.ruta = ruta;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }
}