package ar.edu.unju.fi.poo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cuotas")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer nCuota;
    private double importe;
    private LocalDate fechaVencimiento;
    private String estadoCuota; // Manejado como String ("ADEUDADA", "PAGADA")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Financiada financiada;

    public Cuota() {}

    public Cuota(Integer nCuota, double importe, LocalDate fechaVencimiento, String estadoCuota, Financiada financiada) {
        this.nCuota = nCuota;
        this.importe = importe;
        this.fechaVencimiento = fechaVencimiento;
        this.estadoCuota = estadoCuota;
        this.financiada = financiada;
    }

    public void marcarComoPagada() {
        this.estadoCuota = "PAGADA";
    }

    // Getters y Setters
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

    public Financiada getFinanciada() { return financiada; }
    public void setFinanciada(Financiada financiada) { this.financiada = financiada; }
}