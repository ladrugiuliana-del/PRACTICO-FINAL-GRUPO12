package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaVentaHistorialDTO;
import ar.edu.unju.fi.poo.dto.VentaRequestDTO;
import java.time.LocalDate;
import java.util.List;

public interface VentaService {

    RespuestaVentaHistorialDTO registrarVenta(VentaRequestDTO dto);

    List<RespuestaVentaHistorialDTO> buscarVentasPorFecha(LocalDate inicio, LocalDate fin);
    List<RespuestaVentaHistorialDTO> buscarHistorialPorCliente(String clienteId);

    RespuestaCuotaDTO cobrarSiguienteCuota(String ventaId);
    List<RespuestaCuotaDTO> consultarCuotasPendientesPorCliente(String clienteId);

    List<RespuestaVentaHistorialDTO> buscarVentasContado();
    List<RespuestaVentaHistorialDTO> buscarVentasFinanciadas();
}