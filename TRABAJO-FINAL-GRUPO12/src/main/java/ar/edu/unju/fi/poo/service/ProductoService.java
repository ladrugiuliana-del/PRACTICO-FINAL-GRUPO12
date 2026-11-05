package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> filtrarPorMarca(String marca) {
        return productoRepository.findByMarcaIgnoreCase(marca);
    }

    public List<Producto> filtrarPorCategoria(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<Producto> filtrarPorRangoPrecio(double min, double max) {
        return productoRepository.findByPrecioBaseBetween(min, max);
    }

    public List<Producto> obtenerStockCritico(Integer umbral) {
        return productoRepository.findProductosStockCritico(umbral);
    }

    @Transactional
    public void gestionarStock(Integer productoId, int cantidadVendida) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
        
        producto.actualizarStock(cantidadVendida);
        productoRepository.save(producto);
    }
}
