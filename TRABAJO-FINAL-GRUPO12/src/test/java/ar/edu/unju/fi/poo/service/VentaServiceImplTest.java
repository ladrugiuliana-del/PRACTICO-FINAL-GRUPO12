package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.entity.*;
import ar.edu.unju.fi.poo.impl.VentaServiceImpl;
import ar.edu.unju.fi.poo.repository.*;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CuponRepository cuponRepository;

    @Mock
    private CuotaRepository cuotaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;
    
    private Particular cliente;
    private Producto producto;
    private Detalle detalle;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        cliente = new Particular();
        cliente.setId("20391234567");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");

        categoria = new Categoria(1, "Electrónica");
        producto = new Producto(1, "Smart TV", "Televisor 4K", "Samsung", categoria, 10, 100000.0);

        detalle = new Detalle();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecioBase(producto.getPrecioBase());
    }

    @Test
    void testBuscarVentasPorFecha() {
        LocalDate inicio = LocalDate.of(2026, 11, 1);
        LocalDate fin = LocalDate.of(2026, 11, 30);
        List<Venta> ventasSimuladas = new ArrayList<>();
        
        when(ventaRepository.findByFechaBetween(inicio, fin)).thenReturn(ventasSimuladas);

        List<Venta> resultado = ventaService.buscarVentasPorFecha(inicio, fin);

        assertNotNull(resultado, "La lista de resultados no debería ser nula");
        assertEquals(ventasSimuladas, resultado, "Las ventas devueltas deben coincidir");
    }

    @Test
    void testBuscarHistorialPorCliente() {
        String idClienteFalso = "40123456";
        List<Venta> historialSimulado = new ArrayList<>();
        
        when(ventaRepository.findByClienteId(idClienteFalso)).thenReturn(historialSimulado);

        List<Venta> resultado = ventaService.buscarHistorialPorCliente(idClienteFalso);

        assertNotNull(resultado, "El historial no debe ser nulo");
        assertEquals(historialSimulado, resultado, "Debe devolver las ventas de ese cliente");
    }
    @Test
    void realizarVentaContado_ConCuponValido_AplicaDescuentoYActualizaStock() {
        Cupon cupon = new Cupon("DESC10", 10, LocalDate.now().plusDays(10));

        when(clienteRepository.findById("20391234567")).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(cuponRepository.findByCodigo("DESC10")).thenReturn(Optional.of(cupon));
        when(ventaRepository.save(any(Contado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contado resultado = ventaService.realizarVentaContado("20391234567", List.of(detalle), "DESC10");

        assertNotNull(resultado);
        assertEquals(200000.0, resultado.getSubtotal(), 0.01);
        assertEquals(180000.0, resultado.getTotal(), 0.01);
        assertEquals(8, producto.getStock());
    }

    @Test
    void realizarVentaFinanciada_CalcularRecargoYGeneraPlan6Cuotas() {
        Integer recargo = 20;

        when(clienteRepository.findById("20391234567")).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Financiada.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Financiada resultado = ventaService.realizarVentaFinanciada("20391234567", List.of(detalle), recargo);

        assertNotNull(resultado);
        assertEquals(200000.0, resultado.getSubtotal(), 0.01);
        assertEquals(240000.0, resultado.getTotal(), 0.01);

        List<Cuota> cuotas = resultado.getCuotas();
        assertEquals(6, cuotas.size());

        for (int i = 0; i < 6; i++) {
            Cuota c = cuotas.get(i);
            assertEquals(i + 1, c.getNCuota());
            assertEquals(40000.0, c.getImporte(), 0.01);
            assertEquals("ADEUDADA", c.getEstadoCuota());
            assertEquals(LocalDate.now().plusMonths(i + 1), c.getFechaVencimiento());
        }
    }

    @Test
    void realizarVenta_StockInsuficiente_LanzaExcepcion() {
        detalle.setCantidad(15);

        when(clienteRepository.findById("20391234567")).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class, () ->
            ventaService.realizarVentaContado("20391234567", List.of(detalle), null)
        );
    }
    
    @Test
    void testBuscarVentasContado() {
        List<Contado> ventasContadoSimuladas = List.of(new Contado());
        when(ventaRepository.findByTipoModalidad(Contado.class)).thenReturn(ventasContadoSimuladas);

        List<Contado> resultado = ventaService.buscarVentasContado();

        assertNotNull(resultado, "La lista de ventas al contado no debe ser nula");
        assertEquals(1, resultado.size());
        assertEquals(ventasContadoSimuladas, resultado);
        verify(ventaRepository, times(1)).findByTipoModalidad(Contado.class);
    }

    @Test
    void testBuscarVentasFinanciadas() {
        List<Financiada> ventasFinanciadasSimuladas = List.of(new Financiada());
        when(ventaRepository.findByTipoModalidad(Financiada.class)).thenReturn(ventasFinanciadasSimuladas);

        List<Financiada> resultado = ventaService.buscarVentasFinanciadas();

        assertNotNull(resultado, "La lista de ventas financiadas no debe ser nula");
        assertEquals(1, resultado.size());
        assertEquals(ventasFinanciadasSimuladas, resultado);
        verify(ventaRepository, times(1)).findByTipoModalidad(Financiada.class);
    }
    
    @Test
    void cobrarSiguienteCuota_CuotaVencida_ActualizaAVencidaYLuegoPagada() {
        Financiada venta = new Financiada();
        venta.setCodigoComprobante("COMP-001");
        venta.setTotal(60000.0);
        venta.setFecha(LocalDate.now().minusMonths(2));
        venta.generarPlan6Cuotas(); // Cuota 1 tiene vencimiento hace 1 mes

        when(ventaRepository.findById(10)).thenReturn(Optional.of(venta));

        RespuestaCuotaDTO resultado = ventaService.cobrarSiguienteCuota("10");

        assertEquals(1, resultado.getNCuota());
        assertEquals("PAGADA", resultado.getEstadoCuota());
        assertEquals("PENDIENTE", venta.getEstado());
    }

    @Test
    void cobrarSiguienteCuota_UltimaCuota_CambiaVentaASaldada() {
        Financiada venta = new Financiada();
        venta.setCodigoComprobante("COMP-002");
        venta.setTotal(60000.0);
        venta.setFecha(LocalDate.now());
        venta.generarPlan6Cuotas();
        // Marcamos las primeras 5 como pagadas
        for (int i = 0; i < 5; i++) {
            venta.getCuotas().get(i).marcarComoPagada();
        }

        when(ventaRepository.findById(11)).thenReturn(Optional.of(venta));

        RespuestaCuotaDTO resultado = ventaService.cobrarSiguienteCuota("11");

        assertEquals(6, resultado.getNCuota());
        assertEquals("PAGADA", resultado.getEstadoCuota());
        assertEquals("SALDADA", venta.getEstado());
    }

    @Test
    void cobrarSiguienteCuota_VentaYaSaldada_LanzaExcepcion() {
        Financiada venta = new Financiada();
        venta.setTotal(60000.0);
        venta.generarPlan6Cuotas();
        venta.getCuotas().forEach(Cuota::marcarComoPagada);
        venta.actualizarEstado();

        when(ventaRepository.findById(12)).thenReturn(Optional.of(venta));

        assertThrows(IllegalStateException.class, () -> ventaService.cobrarSiguienteCuota("12"));
    }
}