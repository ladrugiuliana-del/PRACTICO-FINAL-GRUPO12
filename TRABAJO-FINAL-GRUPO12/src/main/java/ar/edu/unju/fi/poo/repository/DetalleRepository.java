package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.entity.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Integer> {
}