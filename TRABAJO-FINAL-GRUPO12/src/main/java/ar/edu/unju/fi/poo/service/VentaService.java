package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.entity.Venta;
import java.time.LocalDate;
import java.util.List;

public interface VentaService {
    
    List<Venta> buscarVentasPorFecha(LocalDate inicio, LocalDate fin);
    
    List<Venta> buscarHistorialPorCliente(String clienteId);
}