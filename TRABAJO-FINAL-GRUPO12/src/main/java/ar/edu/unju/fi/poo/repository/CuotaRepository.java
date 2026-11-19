package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.entity.Cuota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {
    List<Cuota> findByFinanciadaClienteIdAndEstadoCuotaInOrderByNCuotaAsc(String clienteId, List<String> estados);
}