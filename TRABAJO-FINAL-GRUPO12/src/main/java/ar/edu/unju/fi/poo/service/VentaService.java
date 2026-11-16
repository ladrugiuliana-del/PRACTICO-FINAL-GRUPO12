package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.entity.*;

public interface VentaService {
    List<Venta> buscarVentasPorFecha(LocalDate inicio, LocalDate fin);
    List<Venta> buscarHistorialPorCliente(String clienteId);
    
    Contado realizarVentaContado(String clienteId, List<Detalle> detalles, String codigoCupon);
    Financiada realizarVentaFinanciada(String clienteId, List<Detalle> detalles, Integer porcentajeRecargo);
    List<Cuota> consultarCuotasPendientesPorCliente(String clienteId);
    List<Contado> buscarVentasContado();
    List<Financiada> buscarVentasFinanciadas();
}