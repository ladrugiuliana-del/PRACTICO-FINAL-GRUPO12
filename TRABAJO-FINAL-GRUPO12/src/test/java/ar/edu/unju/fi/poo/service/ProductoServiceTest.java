package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.entity.Categoria;
import ar.edu.unju.fi.poo.entity.Producto;
import ar.edu.unju.fi.poo.impl.ProductoServiceImpl;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductoServiceTest {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoServiceImpl productoServiceImpl;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaGuardada;
    private ProductoDto producto1;
    private ProductoDto producto2;

    @BeforeEach
    void setUp() {
        Categoria cat = new Categoria();
        cat.setNombre("Periféricos Test");
        categoriaGuardada = categoriaRepository.save(cat);

        ProductoDto dto1 = new ProductoDto(
                null, "Teclado", "Mecánico", "RedragonTest",
                categoriaGuardada.getId(), 10, 45000.0
        );
        producto1 = productoService.crear(dto1);

        ProductoDto dto2 = new ProductoDto(
                null, "Mouse", "Gamer", "LogitechTest",
                categoriaGuardada.getId(), 2, 20000.0
        );
        producto2 = productoService.crear(dto2);
    }

    @Test
    void testVerificarStockSuficiente() {
        Producto entidad1 = productoServiceImpl.buscarEntidadPorId(producto1.getId());
        assertTrue(entidad1.verificarStock(4));
        assertFalse(entidad1.verificarStock(15));
    }

    @Test
    void testActualizarStockExitoso() {
        productoServiceImpl.gestionarStock(producto1.getId(), 2);

        ProductoDto actualizado = productoService.buscarPorIdDto(producto1.getId());
        assertEquals(8, actualizado.getStock());
    }

    @Test
    void testActualizarStockInsuficienteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            productoServiceImpl.gestionarStock(producto2.getId(), 5);
        });
    }

    @Test
    void testBuscarPorId() {
        ProductoDto encontrado = productoService.buscarPorIdDto(producto1.getId());
        assertEquals("Teclado", encontrado.getNombre());
    }

    @Test
    void testFiltrarPorMarca() {
        List<Producto> resultado = productoRepository.findByMarcaIgnoreCase("redragontest");
        assertEquals(1, resultado.size());
        assertEquals("Teclado", resultado.get(0).getNombre());
    }

    @Test
    void testFiltrarPorCategoria() {
        List<Producto> resultado = productoRepository.findByCategoriaId(categoriaGuardada.getId());
        assertEquals(2, resultado.size());
    }

    @Test
    void testFiltrarPorRangoPrecio() {
        List<Producto> resultado = productoRepository.findByPrecioBaseBetween(40000.0, 50000.0);
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream().anyMatch(p -> p.getId().equals(producto1.getId())));
    }

    @Test
    void testObtenerStockCritico() {
        List<Producto> criticos = productoRepository.findProductosStockCritico(3);
        assertFalse(criticos.isEmpty());
        assertTrue(criticos.stream().anyMatch(p -> p.getId().equals(producto2.getId())));
    }
}