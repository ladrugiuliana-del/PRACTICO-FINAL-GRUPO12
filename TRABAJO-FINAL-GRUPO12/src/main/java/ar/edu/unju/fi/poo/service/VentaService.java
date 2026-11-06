package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.model.Venta;

public interface VentaService {
    
    List<Venta> buscarVentasPorFecha(LocalDate inicio, LocalDate fin);
    
    List<Venta> buscarHistorialPorCliente(String clienteId);
}