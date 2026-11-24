package ar.edu.unju.fi.poo.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.dto.EstadoCuentaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.VentaEstadoCuentaDTO;
import ar.edu.unju.fi.poo.entity.Cliente;
import ar.edu.unju.fi.poo.entity.Cuota;
import ar.edu.unju.fi.poo.entity.Empresa;
import ar.edu.unju.fi.poo.entity.Financiada;
import ar.edu.unju.fi.poo.entity.Particular;
import ar.edu.unju.fi.poo.entity.Venta;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto) {
        if (dto == null || dto.getTipoCliente() == null) {
            return null;
        }

        if ("PARTICULAR".equalsIgnoreCase(dto.getTipoCliente())) {
            Particular p = new Particular();
            p.setId(dto.getDni());
            p.setEmail(dto.getEmail());
            p.setTelefono(dto.getTelefono());
            p.setDireccion(dto.getDireccion());
            p.setDni(dto.getDni());
            p.setNombre(dto.getNombre());
            p.setApellido(dto.getApellido());
            return p;
        } else if ("EMPRESA".equalsIgnoreCase(dto.getTipoCliente())) {
            Empresa e = new Empresa();
            e.setId(dto.getCuit());
            e.setEmail(dto.getEmail());
            e.setTelefono(dto.getTelefono());
            e.setDireccion(dto.getDireccion());
            e.setRazonSocial(dto.getRazonSocial());
            return e;
        }

        return null;
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setEmail(cliente.getEmail());

        if (cliente instanceof Particular p) {
            dto.setTipoCliente("PARTICULAR");
            dto.setIdentificacion(p.getDni());
            dto.setNombreORazonSocial(p.getNombre() + " " + p.getApellido());
        } else if (cliente instanceof Empresa e) {
            dto.setTipoCliente("EMPRESA");
            dto.setIdentificacion(e.getId() != null ? e.getId() : "Sin CUIT registrado");
            dto.setNombreORazonSocial(e.getRazonSocial());
        }

        return dto;
    }

    public EstadoCuentaDTO toEstadoCuentaDTO(Cliente cliente, List<Venta> ventasDelCliente) {
        EstadoCuentaDTO estado = new EstadoCuentaDTO();
        estado.setIdCliente(cliente.getId());
        estado.setEmail(cliente.getEmail());

        if (cliente instanceof Particular p) {
            estado.setNombreORazonSocial(p.getNombre() + " " + p.getApellido());
        } else if (cliente instanceof Empresa e) {
            estado.setNombreORazonSocial(e.getRazonSocial());
        }

        List<VentaEstadoCuentaDTO> historial = new ArrayList<>();
        if (ventasDelCliente != null) {
            for (Venta v : ventasDelCliente) {
                historial.add(toVentaEstadoCuentaDTO(v));
            }
        }

        estado.setHistorialVentas(historial);
        return estado;
    }

    private VentaEstadoCuentaDTO toVentaEstadoCuentaDTO(Venta v) {
        VentaEstadoCuentaDTO ventaDTO = new VentaEstadoCuentaDTO();
        ventaDTO.setIdVenta(v.getId());
        ventaDTO.setFechaVenta(v.getFecha());

        if (v instanceof Financiada ventaFinanciada) {
            List<RespuestaCuotaDTO> listaCuotasDTO = new ArrayList<>();
            if (ventaFinanciada.getCuotas() != null) {
                for (Cuota c : ventaFinanciada.getCuotas()) {
                    listaCuotasDTO.add(toCuotaDTO(c, ventaFinanciada.getCodigoComprobante()));
                }
            }
            ventaDTO.setEstadoGeneral(ventaFinanciada.getEstado());
            ventaDTO.setCuotas(listaCuotasDTO);
        } else {
            ventaDTO.setEstadoGeneral("SALDADA");
            ventaDTO.setCuotas(new ArrayList<>());
        }

        return ventaDTO;
    }

    private RespuestaCuotaDTO toCuotaDTO(Cuota c, String codigoComprobante) {
        RespuestaCuotaDTO cDTO = new RespuestaCuotaDTO();
        cDTO.setId(c.getId());
        cDTO.setNCuota(c.getNCuota());
        cDTO.setImporte(c.getImporte());
        cDTO.setFechaVencimiento(c.getFechaVencimiento());
        cDTO.setEstadoCuota(c.getEstadoCuota());
        cDTO.setCodigoComprobanteVenta(codigoComprobante);
        return cDTO;
    }
}