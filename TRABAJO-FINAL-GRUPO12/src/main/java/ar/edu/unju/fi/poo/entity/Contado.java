package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CONTADO")
@Getter
@NoArgsConstructor
public class Contado extends Venta {

    @Setter
    @ManyToOne
    @JoinColumn(name = "cupon_id")
    private Cupon cupon;

    @Override
    public void calcularTotal() {
        if (cupon != null && cupon.esValido()) {
            double montoDescuento = getSubtotal() * (cupon.getDescuento() / 100.0);
            setTotal(getSubtotal() - montoDescuento);
        } else {
            setTotal(getSubtotal());
        }
    }
}