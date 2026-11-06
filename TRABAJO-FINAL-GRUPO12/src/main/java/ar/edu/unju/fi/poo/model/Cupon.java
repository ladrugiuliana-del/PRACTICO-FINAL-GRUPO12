package ar.edu.unju.fi.poo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cupones")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String codigo;

    private Integer descuento; // Porcentaje de descuento (ej. 10 para 10%)
    private LocalDate vencimiento;

    public Cupon() {}

    public Cupon(String codigo, Integer descuento, LocalDate vencimiento) {
        this.codigo = codigo;
        this.descuento = descuento;
        this.vencimiento = vencimiento;
    }

    public boolean esValido() {
        return vencimiento == null || !LocalDate.now().isAfter(vencimiento);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Integer getDescuento() { return descuento; }
    public void setDescuento(Integer descuento) { this.descuento = descuento; }

    public LocalDate getVencimiento() { return vencimiento; }
    public void setVencimiento(LocalDate vencimiento) { this.vencimiento = vencimiento; }
}