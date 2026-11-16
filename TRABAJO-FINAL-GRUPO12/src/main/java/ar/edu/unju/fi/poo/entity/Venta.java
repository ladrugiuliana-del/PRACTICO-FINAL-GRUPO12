package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ventas")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor
public abstract class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(unique = true)
    private String codigoComprobante;

    @Setter
    private LocalDate fecha;

    @Setter
    private double subtotal;

    @Setter
    private double total;

    @Setter
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<Detalle> detalles = new ArrayList<>();

    public void agregarDetalle(Detalle detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    public abstract void calcularTotal();
}