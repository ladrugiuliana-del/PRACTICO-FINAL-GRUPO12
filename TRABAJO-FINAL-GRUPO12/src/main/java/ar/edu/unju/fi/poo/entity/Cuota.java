package ar.edu.unju.fi.poo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String estadoCuota; // "ADEUDADA", "VENCIDA", "PAGADA"

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


    //Si la cuota está adeudada y la fecha actual supera la fecha de vencimiento, pasa automáticamente a estado VENCIDA.
    public boolean verificarVencimiento() {
        if ("ADEUDADA".equalsIgnoreCase(this.estadoCuota)
                && this.fechaVencimiento != null
                && LocalDate.now().isAfter(this.fechaVencimiento)) {
            this.estadoCuota = "VENCIDA";
            return true;
        }
        return "VENCIDA".equalsIgnoreCase(this.estadoCuota);
    }

    public boolean estaImpaga() {
        return "ADEUDADA".equalsIgnoreCase(this.estadoCuota)
                || "VENCIDA".equalsIgnoreCase(this.estadoCuota);
    }

    public void marcarComoPagada() {
        if ("PAGADA".equalsIgnoreCase(this.estadoCuota)) {
            throw new IllegalStateException("La cuota N° " + this.nCuota + " ya se encuentra pagada.");
        }
        this.estadoCuota = "PAGADA";
    }
}