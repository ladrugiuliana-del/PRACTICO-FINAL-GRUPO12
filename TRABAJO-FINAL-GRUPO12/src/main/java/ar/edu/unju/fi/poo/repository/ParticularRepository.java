package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.model.Particular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticularRepository extends JpaRepository<Particular, String> {
    boolean existsByDni(String dni);
}