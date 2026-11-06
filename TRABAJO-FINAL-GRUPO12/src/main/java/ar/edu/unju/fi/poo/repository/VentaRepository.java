package ar.edu.unju.fi.poo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.poo.model.Venta;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    List<Venta> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    List<Venta> findByClienteId(String clienteId);
    
    @Query("SELECT v FROM Venta v WHERE TYPE(v) = :tipo")
    <Tipo  extends Venta> List<Tipo> findByTipoModalidad(@Param("tipo") Class<Tipo> tipo);
}