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
    public List<Cuota> consultarCuotasPendientesPorCliente(String clienteId) {
        return cuotaRepository.findByFinanciadaClienteIdAndEstadoCuota(clienteId, "ADEUDADA");
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
    	Integer idVentaParsed = Integer.parseInt(ventaId);
    	
        // Busca la venta por su identificador
    	Venta venta = ventaRepository.findById(idVentaParsed)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + ventaId));

        if (!(venta instanceof Financiada)) {
            throw new IllegalStateException("La venta especificada no es financiada y no posee cuotas.");
        }

        Financiada financiada = (Financiada) venta;

        // Busca la primera cuota pendiente (ADEUDADA o VENCIDA) en orden correlativo estricto
        Cuota cuotaACobrar = financiada.getCuotas().stream()
                .filter(c -> "ADEUDADA".equals(c.getEstadoCuota()) || "VENCIDA".equals(c.getEstadoCuota()))
                .sorted((c1, c2) -> Integer.compare(c1.getNCuota(), c2.getNCuota()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No quedan cuotas pendientes para esta venta (la venta ya se encuentra saldada)."));

        // Impacta el pago utilizando el método de la entidad Cuota
        cuotaACobrar.marcarComoPagada();
        cuotaRepository.save(cuotaACobrar);

        // Comprueba si aún quedan cuotas pendientes
        boolean quedanPendientes = financiada.getCuotas().stream()
                .anyMatch(c -> "ADEUDADA".equals(c.getEstadoCuota()) || "VENCIDA".equals(c.getEstadoCuota()));

        // Si ya no quedan pendientes, la venta se considera saldada automáticamente
        ventaRepository.save(financiada);

        // Retorna el DTO con el resultado del cobro
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