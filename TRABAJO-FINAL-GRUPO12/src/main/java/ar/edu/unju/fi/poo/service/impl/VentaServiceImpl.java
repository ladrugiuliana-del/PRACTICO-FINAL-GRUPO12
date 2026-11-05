package ar.edu.unju.fi.poo.service.impl;

import ar.edu.unju.fi.poo.entity.Venta;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Override
    public List<Venta> buscarVentasPorFecha(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaBetween(inicio, fin);
    }

    // este metodo fue el culpable, colgue de hacerlo pero por fin con este no deberia dar problemas las Junit test
    @Override
    public List<Venta> buscarHistorialPorCliente(String clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }
}