package ar.edu.unju.fi.poo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("FINANCIADA")
public class Financiada extends Venta {

    private Integer recargo;

    @OneToMany(mappedBy = "financiada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuota> cuotas = new ArrayList<>();

    public Financiada() {
        super();
    }

    @Override
    public void calcularTotal() {
        double montoRecargo = getSubtotal() * (recargo / 100.0);
        setTotal(getSubtotal() + montoRecargo);
    }

    public void generarPlan6Cuotas() {
        this.cuotas.clear();
        double importeCuota = Math.round((getTotal() / 6.0) * 100.0) / 100.0;
        LocalDate fechaBase = getFecha() != null ? getFecha() : LocalDate.now();

        for (int i = 1; i <= 6; i++) {
            Cuota cuota = new Cuota(
                    i,
                    importeCuota,
                    fechaBase.plusMonths(i),
                    "ADEUDADA",
                    this
            );
            this.cuotas.add(cuota);
        }
    }

    public Integer getRecargo() { return recargo; }
    public void setRecargo(Integer recargo) { this.recargo = recargo; }

    public List<Cuota> getCuotas() { return cuotas; }
    public void setCuotas(List<Cuota> cuotas) { this.cuotas = cuotas; }
}