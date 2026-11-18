package ar.edu.unju.fi.poo.impl;

import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.entity.*;
import ar.edu.unju.fi.poo.repository.*;
import ar.edu.unju.fi.poo.service.VentaService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;
    private final CuotaRepository cuotaRepository;

    @Override
    public List<Venta> buscarVentasPorFecha(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaBetween(inicio, fin);
    }

    @Override
    public List<Venta> buscarHistorialPorCliente(String clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public List<Cuota> consultarCuotasPendientesPorCliente(String clienteId) {
        List<Cuota> pendientes = cuotaRepository.findByFinanciadaClienteIdAndEstadoCuotaInOrderByNCuotaAsc(
                clienteId, List.of("ADEUDADA", "VENCIDA")
        );
        // Actualiza automáticamente a VENCIDA aquellas cuya fecha ya expiró
        for (Cuota c : pendientes) {
            if (c.verificarVencimiento()) {
                cuotaRepository.save(c);
            }
        }
        return pendientes;
    }

    @Override
    @Transactional
    public Contado realizarVentaContado(String clienteId, List<Detalle> detalles, String codigoCupon) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + clienteId));

        validarYActualizarStock(detalles);

        Contado venta = new Contado();
        venta.setCodigoComprobante(UUID.randomUUID().toString());
        venta.setFecha(LocalDate.now());
        venta.setCliente(cliente);

        procesarDetallesYSubtotal(detalles, venta);

        if (codigoCupon != null && !codigoCupon.isBlank()) {
            Cupon cupon = cuponRepository.findByCodigo(codigoCupon).orElse(null);
            venta.setCupon(cupon);
        }

        venta.calcularTotal();
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public Financiada realizarVentaFinanciada(String clienteId, List<Detalle> detalles, Integer porcentajeRecargo) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + clienteId));

        validarYActualizarStock(detalles);

        Financiada venta = new Financiada();
        venta.setCodigoComprobante(UUID.randomUUID().toString());
        venta.setFecha(LocalDate.now());
        venta.setCliente(cliente);
        venta.setRecargo(porcentajeRecargo);

        procesarDetallesYSubtotal(detalles, venta);

        venta.calcularTotal();
        venta.generarPlan6Cuotas();

        return ventaRepository.save(venta);
    }

    // ENDPOINT DE COBRO DE CUOTA
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
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Venta no encontrada con ID: " + ventaId));

        if (!(venta instanceof Financiada financiada)) {
            throw new IllegalStateException("La venta especificada es de contado y no posee plan de cuotas.");
        }

        if ("SALDADA".equalsIgnoreCase(financiada.getEstado())) {
            throw new IllegalStateException("La venta ya se encuentra SALDADA; no tiene cuotas pendientes.");
        }

        // Ordena todas las cuotas por número correlativo estricto (1..6)
        List<Cuota> cuotasOrdenadas = financiada.getCuotas().stream()
                .sorted(java.util.Comparator.comparingInt(Cuota::getNCuota))
                .toList();

        // Obtiene la primera cuota impaga correlativa
        Cuota cuotaACobrar = cuotasOrdenadas.stream()
                .filter(Cuota::estaImpaga)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No quedan cuotas pendientes para esta venta (la venta ya se encuentra saldada)."));

        // impedimos cobrar cuotas futuras si existiera alguna cuota anterior impaga
        boolean existenAnterioresImpagas = cuotasOrdenadas.stream()
                .anyMatch(c -> c.getNCuota() < cuotaACobrar.getNCuota() && c.estaImpaga());
        if (existenAnterioresImpagas) {
            throw new IllegalStateException("No se puede cobrar la cuota N° " + cuotaACobrar.getNCuota()
                    + " porque existen cuotas anteriores impagas.");
        }

        //si superó el vencimiento, pasa automáticamente a VENCIDA antes de registrar el pago
        cuotaACobrar.verificarVencimiento();

        // Registra el pago (pasa a PAGADA)
        cuotaACobrar.marcarComoPagada();
        cuotaRepository.save(cuotaACobrar);

        // Si fue la última cuota adeudada, cambia el estado general de la venta a SALDADA
        financiada.actualizarEstado();
        ventaRepository.save(financiada);

        return new RespuestaCuotaDTO(
                cuotaACobrar.getId(),
                cuotaACobrar.getNCuota(),
                cuotaACobrar.getImporte(),
                cuotaACobrar.getFechaVencimiento(),
                cuotaACobrar.getEstadoCuota(),
                financiada.getCodigoComprobante()
        );
    }

    private void validarYActualizarStock(List<Detalle> detalles) {
        for (Detalle detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            if (!producto.verificarStock(detalle.getCantidad())) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            producto.actualizarStock(detalle.getCantidad());
            productoRepository.save(producto);
        }
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
    public List<Contado> buscarVentasContado() {
        return ventaRepository.findByTipoModalidad(Contado.class);
    }

    @Override
    public List<Financiada> buscarVentasFinanciadas() {
        return ventaRepository.findByTipoModalidad(Financiada.class);
    }
}