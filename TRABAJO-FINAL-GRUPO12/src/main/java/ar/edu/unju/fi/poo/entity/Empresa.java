package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresas")
@Getter
@NoArgsConstructor
public class Empresa extends Cliente {

    @Setter
    @Column(name = "razon_social", nullable = false)
    private String razonSocial;
}