package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.entity.Venta;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Estos son los imports estáticos que te tiraban error antes
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

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
}