package ar.edu.unju.fi.poo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "particulares")
@Getter
@NoArgsConstructor
public class Particular extends Cliente {

    @Setter
    @Column(nullable = false)
    private String nombre;

    @Setter
    @Column(nullable = false)
    private String apellido;

    @Setter
    @Column(nullable = false, unique = true)
    private String dni;
}