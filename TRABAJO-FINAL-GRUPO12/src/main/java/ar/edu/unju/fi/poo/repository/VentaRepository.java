package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;


public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    // Consulta para traer ventas en un rango de fechas (tu tarea)
    List<Venta> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
 // Consulta para el historial por cliente
    List<Venta> findByClienteId(String clienteId);
}