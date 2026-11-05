package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    boolean existsByEmail(String email);
}