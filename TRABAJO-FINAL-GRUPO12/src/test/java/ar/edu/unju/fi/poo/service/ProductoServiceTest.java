package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVerificarStockSuficiente() {
    	Categoria categoria1 = new Categoria(1, "Periféricos");
        Producto producto = new Producto(1, "Teclado", "Mecánico", "Redragon", categoria1, 10, 45000.0);
        assertTrue(producto.verificarStock(4));
        assertFalse(producto.verificarStock(15));
    }

    @Test
    void testActualizarStockExitoso() {
    	Categoria categoria1 = new Categoria(1, "Periféricos");
        Producto producto = new Producto(1, "Teclado", "Mecánico", "Redragon", categoria1, 10, 45000.0);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoService.gestionarStock(1, 2);

        assertEquals(8, producto.getStock());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testActualizarStockInsuficienteLanzaExcepcion() {
    	Categoria categoria1 = new Categoria(1, "Periféricos");
        Producto producto = new Producto(1, "Teclado", "Mecánico", "Redragon", categoria1, 2, 45000.0);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class, () -> {
            productoService.gestionarStock(1, 5);
        });
    }
}