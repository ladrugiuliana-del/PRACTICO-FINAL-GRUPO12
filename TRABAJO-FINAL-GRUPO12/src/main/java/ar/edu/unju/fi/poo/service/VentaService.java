package ar.edu.unju.fi.poo.service;

import java.time.LocalDate;
import java.util.List;

import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaVentaHistorialDTO;
import ar.edu.unju.fi.poo.dto.VentaRequestDTO;

public interface VentaService {

    List<RespuestaVentaHistorialDTO> buscarVentasPorFecha(LocalDate inicio, LocalDate fin);
    List<RespuestaVentaHistorialDTO> buscarHistorialPorCliente(String clienteId);

    RespuestaVentaHistorialDTO registrarVenta(VentaRequestDTO dto);
    RespuestaVentaHistorialDTO realizarVentaContado(VentaRequestDTO dto);
    RespuestaVentaHistorialDTO realizarVentaFinanciada(VentaRequestDTO dto, Integer porcentajeRecargo);

    RespuestaCuotaDTO cobrarSiguienteCuota(String ventaId);
    List<RespuestaCuotaDTO> consultarCuotasPendientesPorCliente(String clienteId);

    List<RespuestaVentaHistorialDTO> buscarVentasContado();
    List<RespuestaVentaHistorialDTO> buscarVentasFinanciadas();
}