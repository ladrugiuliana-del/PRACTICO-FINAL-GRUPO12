package ar.edu.unju.fi.poo.service.impl;

import ar.edu.unju.fi.poo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.unju.fi.poo.repository.*;
import ar.edu.unju.fi.poo.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;
    private final CuotaRepository cuotaRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            ClienteRepository clienteRepository,
                            ProductoRepository productoRepository,
                            CuponRepository cuponRepository,
                            CuotaRepository cuotaRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.cuponRepository = cuponRepository;
        this.cuotaRepository = cuotaRepository;
    }

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