package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false, length = 100)
    private String nombre;

    @Setter
    @Column(length = 255)
    private String descripcion;

    @Setter
    @Column(nullable = false, length = 50)
    private String marca;

    @Setter
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Setter
    @Column(nullable = false)
    private Integer stock;

    @Setter
    @Column(nullable = false)
    private double precioBase;

    public boolean verificarStock(int cantidadRequerida) {
        return this.stock >= cantidadRequerida;
    }

    public void actualizarStock(int cantidadVendida) {
        if (verificarStock(cantidadVendida)) {
            this.stock -= cantidadVendida;
        } else {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + this.nombre);
        }
    }
}
