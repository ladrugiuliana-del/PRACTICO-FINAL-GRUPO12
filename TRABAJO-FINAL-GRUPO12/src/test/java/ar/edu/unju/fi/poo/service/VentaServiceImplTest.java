package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.*;
import ar.edu.unju.fi.poo.repository.*;
import ar.edu.unju.fi.poo.service.impl.VentaServiceImpl;
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
}