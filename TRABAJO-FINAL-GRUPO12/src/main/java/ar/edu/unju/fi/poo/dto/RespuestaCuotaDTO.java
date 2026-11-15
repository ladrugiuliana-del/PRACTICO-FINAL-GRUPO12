package ar.edu.unju.fi.poo.dto;

import java.time.LocalDate;

public class RespuestaCuotaDTO {

    private Integer id;
    private Integer nCuota;
    private double importe;
    private LocalDate fechaVencimiento;
    private String estadoCuota;
    private String codigoComprobanteVenta;

    public RespuestaCuotaDTO() {}

    public RespuestaCuotaDTO(Integer id, Integer nCuota, double importe, LocalDate fechaVencimiento, String estadoCuota, String codigoComprobanteVenta) {
        this.id = id;
        this.nCuota = nCuota;
        this.importe = importe;
        this.fechaVencimiento = fechaVencimiento;
        this.estadoCuota = estadoCuota;
        this.codigoComprobanteVenta = codigoComprobanteVenta;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getNCuota() { return nCuota; }
    public void setNCuota(Integer nCuota) { this.nCuota = nCuota; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getEstadoCuota() { return estadoCuota; }
    public void setEstadoCuota(String estadoCuota) { this.estadoCuota = estadoCuota; }

    public String getCodigoComprobanteVenta() { return codigoComprobanteVenta; }
    public void setCodigoComprobanteVenta(String codigoComprobanteVenta) { this.codigoComprobanteVenta = codigoComprobanteVenta; }
}