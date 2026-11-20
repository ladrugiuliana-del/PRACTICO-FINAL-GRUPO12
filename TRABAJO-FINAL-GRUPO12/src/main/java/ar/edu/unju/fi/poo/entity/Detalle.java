package ar.edu.unju.fi.poo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalles")
@Getter
@NoArgsConstructor
public class Detalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private Integer cantidad;

    @Setter
    private double precioBase;

    @Setter
    private double subtotal;

    @Setter
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @Setter
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioBase;
    }
}