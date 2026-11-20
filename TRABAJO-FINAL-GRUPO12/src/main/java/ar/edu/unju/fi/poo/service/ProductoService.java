package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.entity.Categoria;
import ar.edu.unju.fi.poo.entity.Producto;
import ar.edu.unju.fi.poo.exceptions.ProductoException;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    // MÉTODOS ORIGINALES (TP10) no tocar si lo hace se rompe todo xd

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

    // NUEVOS MÉTODOS PARA LA API REST (TP11) - CON PATRÓN DTO (mi parte la del integrante 2 uwu)

    private ProductoDto mapearADto(Producto producto) {
        ProductoDto dto = new ProductoDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setMarca(producto.getMarca());
        dto.setStock(producto.getStock());
        dto.setPrecioBase(producto.getPrecioBase());
        
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
        }
        return dto;
    }

    private List<ProductoDto> mapearLista(List<Producto> productos) {
        List<ProductoDto> dtos = new ArrayList<>();
        for (Producto p : productos) {
            dtos.add(mapearADto(p));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> listar() {
        return mapearLista(productoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> filtrarProductos(Integer categoriaId, String marca, Double precioMin, Double precioMax) {
        return mapearLista(productoRepository.filtrarProductos(categoriaId, marca, precioMin, precioMax));
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> buscarPorStockCritico(Integer umbral) {
        return mapearLista(productoRepository.findProductosStockCritico(umbral)); 
    }

    @Transactional(readOnly = true)
    public Producto buscarEntidadPorId(Integer id) {
        return productoRepository.findById(id).orElseThrow(
                () -> new ProductoException(HttpStatus.NOT_FOUND, "Producto no encontrado")
        );
    }

    @Transactional(readOnly = true)
    public ProductoDto buscarPorIdDto(Integer id) {
        return mapearADto(buscarEntidadPorId(id));
    }

    @Transactional
    public ProductoDto crear(ProductoDto dto) {
        if (dto == null || dto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        // Buscar la categoría obligatoria en la BD
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoriaId()));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setMarca(dto.getMarca());
        producto.setStock(dto.getStock());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setCategoria(categoria);

        producto = productoRepository.save(producto);
        return mapearADto(producto);
    }

    @Transactional
    public ProductoDto actualizar(Integer id, ProductoDto dto) {
        if (dto == null || dto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        Producto productoExistente = buscarEntidadPorId(id);

        // Buscar la categoría para la actualización también
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoriaId()));

        productoExistente.setNombre(dto.getNombre());
        productoExistente.setDescripcion(dto.getDescripcion());
        productoExistente.setMarca(dto.getMarca());
        productoExistente.setStock(dto.getStock());
        productoExistente.setPrecioBase(dto.getPrecioBase());
        productoExistente.setCategoria(categoria);

        productoExistente = productoRepository.save(productoExistente);
        return mapearADto(productoExistente);
    }

    @Transactional
    public void eliminar(Integer id) {
        Producto producto = buscarEntidadPorId(id);
        productoRepository.delete(producto);
    }
}