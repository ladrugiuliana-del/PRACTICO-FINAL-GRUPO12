package ar.edu.unju.fi.poo.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CONTADO")
public class Contado extends Venta {

    @ManyToOne
    @JoinColumn(name = "cupon_id")
    private Cupon cupon;

    public Contado() {
        super();
    }

    @Override
    public void calcularTotal() {
        if (cupon != null && cupon.esValido()) {
            double montoDescuento = getSubtotal() * (cupon.getDescuento() / 100.0);
            setTotal(getSubtotal() - montoDescuento);
        } else {
            setTotal(getSubtotal());
        }
    }

    public Cupon getCupon() { return cupon; }
    public void setCupon(Cupon cupon) { this.cupon = cupon; }
}