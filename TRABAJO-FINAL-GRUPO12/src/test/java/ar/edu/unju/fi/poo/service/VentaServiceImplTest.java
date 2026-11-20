package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.DetalleVentaRequestDTO;
import ar.edu.unju.fi.poo.dto.RespuestaCuotaDTO;
import ar.edu.unju.fi.poo.dto.RespuestaVentaHistorialDTO;
import ar.edu.unju.fi.poo.dto.VentaRequestDTO;
import ar.edu.unju.fi.poo.entity.*;
import ar.edu.unju.fi.poo.impl.VentaServiceImpl;
import ar.edu.unju.fi.poo.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    private DetalleVentaRequestDTO detalleDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        cliente = new Particular();
        cliente.setId("20391234567");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");

        categoria = new Categoria(1, "Electrónica");
        producto = new Producto(1, "Smart TV", "Televisor 4K", "Samsung", categoria, 10, 100000.0);

        detalleDTO = new DetalleVentaRequestDTO(1, 2);
    }

    @Test
    void testBuscarVentasPorFecha() {
        LocalDate inicio = LocalDate.of(2026, 11, 1);
        LocalDate fin = LocalDate.of(2026, 11, 30);

        Contado venta = new Contado();
        venta.setCodigoComprobante("C-001");
        venta.setFecha(LocalDate.of(2026, 11, 10));
        venta.setSubtotal(100000.0);
        venta.setTotal(100000.0);

        when(ventaRepository.findByFechaBetween(inicio, fin)).thenReturn(List.of(venta));

        List<RespuestaVentaHistorialDTO> resultado = ventaService.buscarVentasPorFecha(inicio, fin);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("C-001", resultado.get(0).getCodigoComprobante());
    }

    @Test
    void testBuscarHistorialPorCliente() {
        String clienteId = "20391234567";
        Financiada venta = new Financiada();
        venta.setCodigoComprobante("F-002");
        venta.setFecha(LocalDate.now());
        venta.setSubtotal(100000.0);
        venta.setTotal(115000.0);

        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        when(ventaRepository.findByClienteId(clienteId)).thenReturn(List.of(venta));

        List<RespuestaVentaHistorialDTO> resultado = ventaService.buscarHistorialPorCliente(clienteId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("FINANCIADA", resultado.get(0).getModalidadPago());
    }

    @Test
    void realizarVentaContado_ConCuponValido_AplicaDescuentoYActualizaStock() {
        Cupon cupon = new Cupon("DESC10", 10, LocalDate.now().plusDays(10));
        VentaRequestDTO request = new VentaRequestDTO("20391234567", "CONTADO", "DESC10", List.of(detalleDTO));

        when(clienteRepository.findById("20391234567")).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(cuponRepository.findByCodigo("DESC10")).thenReturn(Optional.of(cupon));
        when(ventaRepository.save(any(Contado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RespuestaVentaHistorialDTO resultado = ventaService.realizarVentaContado(request);

        assertNotNull(resultado);
        assertEquals(200000.0, resultado.getSubtotal(), 0.01);
        assertEquals(180000.0, resultado.getTotal(), 0.01);
        assertEquals(8, producto.getStock());
    }

    @Test
    void realizarVentaFinanciada_CalcularRecargoYGeneraPlan6Cuotas() {
        Integer recargo = 20;
        VentaRequestDTO request = new VentaRequestDTO("20391234567", "FINANCIADA", null, List.of(detalleDTO));

        when(clienteRepository.findById("20391234567")).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Financiada.class))).thenAnswer(invocation -> {
            Financiada f = invocation.getArgument(0);
            assertEquals(6, f.getCuotas().size());
            for (int i = 0; i < 6; i++) {
                Cuota c = f.getCuotas().get(i);
                assertEquals(i + 1, c.getNCuota());
                assertEquals(40000.0, c.getImporte(), 0.01);
                assertEquals("ADEUDADA", c.getEstadoCuota());
                assertEquals(LocalDate.now().plusMonths(i + 1), c.getFechaVencimiento());
            }
            return f;
        });

        RespuestaVentaHistorialDTO resultado = ventaService.realizarVentaFinanciada(request, recargo);

        assertNotNull(resultado);
        assertEquals(200000.0, resultado.getSubtotal(), 0.01);
        assertEquals(240000.0, resultado.getTotal(), 0.01);
    }

    @Test
    void realizarVenta_StockInsuficiente_LanzaExcepcion() {
        detalleDTO.setCantidad(15);
        VentaRequestDTO request = new VentaRequestDTO("20391234567", "CONTADO", null, List.of(detalleDTO));

        when(clienteRepository.findById("20391234567")).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class, () ->
            ventaService.realizarVentaContado(request)
        );
    }

    @Test
    void testBuscarVentasContado() {
        Contado venta = new Contado();
        venta.setCodigoComprobante("C-003");
        venta.setFecha(LocalDate.now());
        when(ventaRepository.findByTipoModalidad(Contado.class)).thenReturn(List.of(venta));

        List<RespuestaVentaHistorialDTO> resultado = ventaService.buscarVentasContado();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByTipoModalidad(Contado.class);
    }

    @Test
    void testBuscarVentasFinanciadas() {
        Financiada venta = new Financiada();
        venta.setCodigoComprobante("F-004");
        venta.setFecha(LocalDate.now());
        when(ventaRepository.findByTipoModalidad(Financiada.class)).thenReturn(List.of(venta));

        List<RespuestaVentaHistorialDTO> resultado = ventaService.buscarVentasFinanciadas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByTipoModalidad(Financiada.class);
    }

    @Test
    void cobrarSiguienteCuota_CuotaVencida_ActualizaAVencidaYLuegoPagada() {
        Financiada venta = new Financiada();
        venta.setCodigoComprobante("COMP-001");
        venta.setTotal(60000.0);
        venta.setFecha(LocalDate.now().minusMonths(2));
        venta.generarPlan6Cuotas();

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