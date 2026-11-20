package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cupones")
@Getter
@NoArgsConstructor
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(unique = true, nullable = false)
    private String codigo;

    @Setter
    private Integer descuento; // Porcentaje de descuento (ej. 10 para 10%)

    @Setter
    private LocalDate vencimiento;

    public Cupon(String codigo, Integer descuento, LocalDate vencimiento) {
        this.codigo = codigo;
        this.descuento = descuento;
        this.vencimiento = vencimiento;
    }

    public boolean esValido() {
        return vencimiento == null || !LocalDate.now().isAfter(vencimiento);
    }
}