package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.*;
import java.time.LocalDate;
import java.util.List;

public interface VentaService {
    List<Venta> buscarVentasPorFecha(LocalDate inicio, LocalDate fin);
    List<Venta> buscarHistorialPorCliente(String clienteId);
    
    Contado realizarVentaContado(String clienteId, List<Detalle> detalles, String codigoCupon);
    Financiada realizarVentaFinanciada(String clienteId, List<Detalle> detalles, Integer porcentajeRecargo);
    List<Cuota> consultarCuotasPendientesPorCliente(String clienteId);
}