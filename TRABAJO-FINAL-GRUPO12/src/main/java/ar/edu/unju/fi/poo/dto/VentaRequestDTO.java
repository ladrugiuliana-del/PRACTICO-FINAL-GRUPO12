package ar.edu.unju.fi.poo.dto;

import java.util.List;

public class VentaRequestDTO {
    private String clienteId; // tipo de identificador que tenga Cliente
    private String tipoPago;   // "CONTADO" o "FINANCIADA"
    private String codigoCupon; // Opcional para contado
    private List<DetalleVentaRequestDTO> detalles;

    // Getters y Setters
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    public String getCodigoCupon() { return codigoCupon; }
    public void setCodigoCupon(String codigoCupon) { this.codigoCupon = codigoCupon; }
    public List<DetalleVentaRequestDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaRequestDTO> detalles) { this.detalles = detalles; }
}