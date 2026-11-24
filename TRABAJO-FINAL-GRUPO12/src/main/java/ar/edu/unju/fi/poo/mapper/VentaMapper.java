package ar.edu.unju.fi.poo.mapper;

import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaVentaHistorialDTO;
import ar.edu.unju.fi.poo.entity.Contado;
import ar.edu.unju.fi.poo.entity.Cuota;
import ar.edu.unju.fi.poo.entity.Venta;
import org.springframework.stereotype.Component;

@Component
public class VentaMapper {

    public RespuestaVentaHistorialDTO toHistorialDTO(Venta venta) {
        return new RespuestaVentaHistorialDTO(
                venta.getId(),
                venta.getCodigoComprobante(),
                venta.getFecha(),
                (venta instanceof Contado) ? "CONTADO" : "FINANCIADA",
                venta.getSubtotal(),
                venta.getTotal(),
                (venta.getDetalles() != null) ? venta.getDetalles().size() : 0
        );
    }

    public RespuestaCuotaDTO toCuotaDTO(Cuota cuota) {
        return new RespuestaCuotaDTO(
                cuota.getId(),
                cuota.getNCuota(),
                cuota.getImporte(),
                cuota.getFechaVencimiento(),
                cuota.getEstadoCuota(),
                (cuota.getFinanciada() != null) ? cuota.getFinanciada().getCodigoComprobante() : null
        );
    }
}