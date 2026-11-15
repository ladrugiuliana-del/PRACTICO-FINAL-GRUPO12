package ar.edu.unju.fi.poo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.model.Venta;

@SpringBootTest
public class VentaRepositoryTest {

    @Autowired
    private VentaRepository ventaRepository;

    @Test
    void testFindByFechaBetween() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 12, 31);

        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);

        assertThat(ventas).isNotNull();
    }
}