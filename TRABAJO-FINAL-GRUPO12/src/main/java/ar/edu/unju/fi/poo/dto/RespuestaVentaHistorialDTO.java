package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;

public class RespuestaVentaHistorialDTO {

    private Integer id;
    private String codigoComprobante;
    private LocalDate fecha;
    private String modalidadPago;
    private double subtotal;
    private double total;
    private int cantidadItems;

    public RespuestaVentaHistorialDTO() {}

    public RespuestaVentaHistorialDTO(Integer id, String codigoComprobante, LocalDate fecha, String modalidadPago, double subtotal, double total, int cantidadItems) {
        this.id = id;
        this.codigoComprobante = codigoComprobante;
        this.fecha = fecha;
        this.modalidadPago = modalidadPago;
        this.subtotal = subtotal;
        this.total = total;
        this.cantidadItems = cantidadItems;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigoComprobante() { return codigoComprobante; }
    public void setCodigoComprobante(String codigoComprobante) { this.codigoComprobante = codigoComprobante; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getModalidadPago() { return modalidadPago; }
    public void setModalidadPago(String modalidadPago) { this.modalidadPago = modalidadPago; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getCantidadItems() { return cantidadItems; }
    public void setCantidadItems(int cantidadItems) { this.cantidadItems = cantidadItems; }
}