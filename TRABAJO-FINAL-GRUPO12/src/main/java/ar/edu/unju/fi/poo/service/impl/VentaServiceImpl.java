package ar.edu.unju.fi.poo.service.impl;

import ar.edu.unju.fi.poo.model.Venta;
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

    @Override
    public List<Venta> buscarHistorialPorCliente(String clienteId) {
        return ventaRepository.findByClienteId(clienteId);

    }
}