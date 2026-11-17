package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "cuotas")
@Getter
@NoArgsConstructor
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private Integer nCuota;

    @Setter
    private double importe;

    @Setter
    private LocalDate fechaVencimiento;

    @Setter
    private String estadoCuota; // Manejado como String ("ADEUDADA", "PAGADA")

    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Financiada financiada;

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
}