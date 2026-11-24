package ar.edu.unju.fi.poo.impl;

import ar.edu.unju.fi.poo.dto.DetalleVentaRequestDTO;
import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaVentaHistorialDTO;
import ar.edu.unju.fi.poo.dto.VentaRequestDTO;
import ar.edu.unju.fi.poo.entity.*;
import ar.edu.unju.fi.poo.mapper.VentaMapper;
import ar.edu.unju.fi.poo.repository.*;
import ar.edu.unju.fi.poo.service.VentaService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private static final int RECARGO_FINANCIADA_DEFECTO = 15;

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;
    private final CuotaRepository cuotaRepository;
    private final VentaMapper ventaMapper;

    @Override
    @Transactional
    public RespuestaVentaHistorialDTO registrarVenta(VentaRequestDTO dto) {
        validarRequestVenta(dto);

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + dto.getClienteId()));

        List<Detalle> detalles = construirYValidarDetalles(dto.getDetalles());

        String tipoPago = dto.getTipoPago().trim().toUpperCase();
        Venta ventaGuardada;

        if ("CONTADO".equals(tipoPago)) {
            Contado venta = new Contado();
            configurarVentaBase(venta, cliente, detalles);

            if (dto.getCodigoCupon() != null && !dto.getCodigoCupon().isBlank()) {
                Cupon cupon = cuponRepository.findByCodigo(dto.getCodigoCupon()).orElse(null);
                venta.setCupon(cupon);
            }

            venta.calcularTotal();
            ventaGuardada = ventaRepository.save(venta);
        } else if ("FINANCIADA".equals(tipoPago)) {
            Financiada venta = new Financiada();
            configurarVentaBase(venta, cliente, detalles);
            venta.setRecargo(RECARGO_FINANCIADA_DEFECTO);

            venta.calcularTotal();
            venta.generarPlan6Cuotas();
            ventaGuardada = ventaRepository.save(venta);
        } else {
            throw new IllegalArgumentException("Tipo de pago no válido: " + dto.getTipoPago() + ". Use CONTADO o FINANCIADA.");
        }

        return ventaMapper.toHistorialDTO(ventaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaVentaHistorialDTO> buscarVentasPorFecha(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaBetween(inicio, fin).stream()
                .map(ventaMapper::toHistorialDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaVentaHistorialDTO> buscarHistorialPorCliente(String clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException("No se encontró el cliente con ID: " + clienteId);
        }
        return ventaRepository.findByClienteId(clienteId).stream()
                .map(ventaMapper::toHistorialDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<RespuestaCuotaDTO> consultarCuotasPendientesPorCliente(String clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException("No se encontró el cliente con ID: " + clienteId);
        }

        List<Cuota> pendientes = cuotaRepository.findByFinanciadaClienteIdAndEstadoCuotaInOrderByNCuotaAsc(
                clienteId, List.of("ADEUDADA", "VENCIDA")
        );

        for (Cuota c : pendientes) {
            if (c.verificarVencimiento()) {
                cuotaRepository.save(c);
            }
        }

        return pendientes.stream()
                .map(ventaMapper::toCuotaDTO)
                .toList();
    }

    @Override
    @Transactional
    public RespuestaCuotaDTO cobrarSiguienteCuota(String ventaId) {
        Integer idVentaParsed;
        try {
            idVentaParsed = Integer.parseInt(ventaId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El identificador de la venta debe ser numérico: " + ventaId);
        }

        Venta venta = ventaRepository.findById(idVentaParsed)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + ventaId));

        if (!(venta instanceof Financiada financiada)) {
            throw new IllegalStateException("La venta especificada es de contado y no posee plan de cuotas.");
        }

        if ("SALDADA".equalsIgnoreCase(financiada.getEstado())) {
            throw new IllegalStateException("La venta ya se encuentra SALDADA; no tiene cuotas pendientes.");
        }

        List<Cuota> cuotasOrdenadas = financiada.getCuotas().stream()
                .sorted(Comparator.comparingInt(Cuota::getNCuota))
                .toList();

        Cuota cuotaACobrar = cuotasOrdenadas.stream()
                .filter(Cuota::estaImpaga)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No quedan cuotas pendientes para esta venta (la venta ya se encuentra saldada)."));

        boolean existenAnterioresImpagas = cuotasOrdenadas.stream()
                .anyMatch(c -> c.getNCuota() < cuotaACobrar.getNCuota() && c.estaImpaga());
        if (existenAnterioresImpagas) {
            throw new IllegalStateException("No se puede cobrar la cuota N° " + cuotaACobrar.getNCuota()
                    + " porque existen cuotas anteriores impagas.");
        }

        cuotaACobrar.verificarVencimiento();
        cuotaACobrar.marcarComoPagada();
        cuotaRepository.save(cuotaACobrar);

        financiada.actualizarEstado();
        ventaRepository.save(financiada);

        return ventaMapper.toCuotaDTO(cuotaACobrar);
    }

    private void configurarVentaBase(Venta venta, Cliente cliente, List<Detalle> detalles) {
        venta.setCodigoComprobante(UUID.randomUUID().toString());
        venta.setFecha(LocalDate.now());
        venta.setCliente(cliente);
        procesarDetallesYSubtotal(detalles, venta);
    }

    private void validarRequestVenta(VentaRequestDTO dto) {
        if (dto == null || dto.getClienteId() == null || dto.getClienteId().isBlank()) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio.");
        }
        if (dto.getTipoPago() == null || dto.getTipoPago().isBlank()) {
            throw new IllegalArgumentException("El tipo de pago es obligatorio (CONTADO o FINANCIADA).");
        }
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un detalle de producto.");
        }
    }

    private List<Detalle> construirYValidarDetalles(List<DetalleVentaRequestDTO> detallesDTO) {
        List<Detalle> detallesConstruidos = new ArrayList<>();

        for (DetalleVentaRequestDTO item : detallesDTO) {
            if (item.getProductoId() == null || item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada detalle debe indicar un productoId válido y una cantidad mayor a 0.");
            }

            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + item.getProductoId()));

            if (!producto.verificarStock(item.getCantidad())) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.actualizarStock(item.getCantidad());
            productoRepository.save(producto);

            Detalle detalle = new Detalle();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioBase(producto.getPrecioBase());
            detallesConstruidos.add(detalle);
        }

        return detallesConstruidos;
    }

    private void procesarDetallesYSubtotal(List<Detalle> detalles, Venta venta) {
        double subtotalAcumulado = 0.0;
        for (Detalle detalle : detalles) {
            detalle.calcularSubtotal();
            subtotalAcumulado += detalle.getSubtotal();
            venta.agregarDetalle(detalle);
        }
        venta.setSubtotal(subtotalAcumulado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaVentaHistorialDTO> buscarVentasContado() {
        return ventaRepository.findByTipoModalidad(Contado.class).stream()
                .map(ventaMapper::toHistorialDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaVentaHistorialDTO> buscarVentasFinanciadas() {
        return ventaRepository.findByTipoModalidad(Financiada.class).stream()
                .map(ventaMapper::toHistorialDTO)
                .toList();
    }
}