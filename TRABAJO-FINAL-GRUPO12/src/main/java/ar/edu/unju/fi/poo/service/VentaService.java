package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.entity.*;

public interface VentaService {
    List<Venta> buscarVentasPorFecha(LocalDate inicio, LocalDate fin);
    List<Venta> buscarHistorialPorCliente(String clienteId);
    
    Contado realizarVentaContado(String clienteId, List<Detalle> detalles, String codigoCupon);
    Financiada realizarVentaFinanciada(String clienteId, List<Detalle> detalles, Integer porcentajeRecargo);
    
    // Método correspondiente al Endpoint de Cobro de Cuota (Integrante 2)
    RespuestaCuotaDTO cobrarSiguienteCuota(String ventaId);
    
    List<Cuota> consultarCuotasPendientesPorCliente(String clienteId);
    List<Contado> buscarVentasContado();
    List<Financiada> buscarVentasFinanciadas();
}