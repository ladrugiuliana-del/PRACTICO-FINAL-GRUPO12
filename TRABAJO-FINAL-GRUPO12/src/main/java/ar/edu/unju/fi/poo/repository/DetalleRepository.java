package ar.edu.unju.fi.poo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.poo.model.Detalle;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Integer> {
}