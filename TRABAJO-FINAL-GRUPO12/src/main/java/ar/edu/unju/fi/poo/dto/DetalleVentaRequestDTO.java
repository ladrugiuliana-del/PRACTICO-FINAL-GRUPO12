package ar.edu.unju.fi.poo.dto;

public class DetalleVentaRequestDTO {
    private Integer productoId;
    private Integer cantidad;

    // Getters y Setters
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
