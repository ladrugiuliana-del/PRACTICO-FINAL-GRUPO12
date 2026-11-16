package ar.edu.unju.fi.poo.entity;

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