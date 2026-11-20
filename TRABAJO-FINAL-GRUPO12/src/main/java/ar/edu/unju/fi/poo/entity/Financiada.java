package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("FINANCIADA")
@Getter
@NoArgsConstructor
public class Financiada extends Venta {

    @Setter
    private Integer recargo;

    @Setter
    private String estado = "PENDIENTE"; // "PENDIENTE" o "SALDADA"

    @OneToMany(mappedBy = "financiada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuota> cuotas = new ArrayList<>();

    @Override
    public void calcularTotal() {
        double montoRecargo = getSubtotal() * (recargo / 100.0);
        setTotal(getSubtotal() + montoRecargo);
    }

    public void generarPlan6Cuotas() {
        this.cuotas.clear();
        this.estado = "PENDIENTE";
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

    //Verifica si existen cuotas impagas (ADEUDADA o VENCIDA). Si no quedan compromisos pendientes, cambia el estado general a SALDADA.
     
    public void actualizarEstado() {
        boolean quedanPendientes = this.cuotas.stream()
                .anyMatch(Cuota::estaImpaga);
        this.estado = quedanPendientes ? "PENDIENTE" : "SALDADA";
    }
}