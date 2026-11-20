package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.entity.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

    @Query("SELECT c FROM Cuota c " +
           "WHERE c.financiada.cliente.id = :clienteId " +
           "AND c.estadoCuota IN :estados " +
           "ORDER BY c.nCuota ASC")
    List<Cuota> findByFinanciadaClienteIdAndEstadoCuotaInOrderByNCuotaAsc(
            @Param("clienteId") String clienteId,
            @Param("estados") List<String> estados
    );
}