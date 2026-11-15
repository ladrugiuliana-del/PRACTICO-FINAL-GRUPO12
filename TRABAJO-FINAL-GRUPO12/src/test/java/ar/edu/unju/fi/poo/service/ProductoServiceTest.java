package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.Categoria;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductoServiceTest {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaGuardada;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        // se guarda  en MySQL (sin ID fijo para que sea auto_increment, antes era porque lo haciamos a mano etc espero esto no cause drama)
        Categoria cat = new Categoria();
        cat.setNombre("Periféricos Test");
        categoriaGuardada = categoriaRepository.save(cat);

        // Producto 1 en la BD (Stock normal: 10)
        Producto p1 = new Producto();
        p1.setNombre("Teclado");
        p1.setDescripcion("Mecánico");
        p1.setMarca("RedragonTest");
        p1.setCategoria(categoriaGuardada);
        p1.setStock(10);
        p1.setPrecioBase(45000.0);
        producto1 = productoService.guardarProducto(p1);

        // Producto 2 en la BD (Stock crítico: 2)
        Producto p2 = new Producto();
        p2.setNombre("Mouse");
        p2.setDescripcion("Gamer");
        p2.setMarca("LogitechTest");
        p2.setCategoria(categoriaGuardada);
        p2.setStock(2);
        p2.setPrecioBase(20000.0);
        producto2 = productoService.guardarProducto(p2);
    }


    @Test
    void testVerificarStockSuficiente() {
        assertTrue(producto1.verificarStock(4));
        assertFalse(producto1.verificarStock(15));
    }

    @Test
    void testActualizarStockExitoso() {
        productoService.gestionarStock(producto1.getId(), 2);

        Optional<Producto> actualizado = productoService.buscarPorId(producto1.getId());
        assertTrue(actualizado.isPresent());
        assertEquals(8, actualizado.get().getStock());
    }

    @Test
    void testActualizarStockInsuficienteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            productoService.gestionarStock(producto2.getId(), 5);
        });
    }

    // nuevos testeos (estos son los que en teoria habian faltado antes unu penon)

    @Test
    void testBuscarPorId() {
        Optional<Producto> encontrado = productoService.buscarPorId(producto1.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Teclado", encontrado.get().getNombre());
    }

    @Test
    void testFiltrarPorMarca() {
        List<Producto> resultado = productoService.filtrarPorMarca("redragontest");
        assertEquals(1, resultado.size());
        assertEquals("Teclado", resultado.get(0).getNombre());
    }

    @Test
    void testFiltrarPorCategoria() {
        List<Producto> resultado = productoService.filtrarPorCategoria(categoriaGuardada.getId());
        assertEquals(2, resultado.size());
    }

    @Test
    void testFiltrarPorRangoPrecio() {
        List<Producto> resultado = productoService.filtrarPorRangoPrecio(40000.0, 50000.0);
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream().anyMatch(p -> p.getId().equals(producto1.getId())));
    }

    @Test
    void testObtenerStockCritico() {
        List<Producto> criticos = productoService.obtenerStockCritico(3);
        assertFalse(criticos.isEmpty());
        assertTrue(criticos.stream().anyMatch(p -> p.getId().equals(producto2.getId())));
    }
}