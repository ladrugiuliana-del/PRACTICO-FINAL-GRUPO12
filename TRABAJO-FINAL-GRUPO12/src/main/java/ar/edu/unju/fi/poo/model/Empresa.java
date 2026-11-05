package ar.edu.unju.fi.poo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas")
public class Empresa extends Cliente {

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    public Empresa() {
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
}