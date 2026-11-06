package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.model.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {
    List<Cuota> findByFinanciadaClienteIdAndEstadoCuota(String clienteId, String estadoCuota);
}