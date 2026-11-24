package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.entity.Categoria;
import ar.edu.unju.fi.poo.entity.Producto;
import ar.edu.unju.fi.poo.exceptions.ProductoException;
import ar.edu.unju.fi.poo.mapper.ProductoMapper;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
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
    private final ProductoMapper productoMapper;

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

    @Transactional(readOnly = true)
    public List<ProductoDto> listar() {
        return productoMapper.toDTOList(productoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> filtrarProductos(Integer categoriaId, String marca, Double precioMin, Double precioMax) {
        return productoMapper.toDTOList(productoRepository.filtrarProductos(categoriaId, marca, precioMin, precioMax));
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> buscarPorStockCritico(Integer umbral) {
        return productoMapper.toDTOList(productoRepository.findProductosStockCritico(umbral));
    }

    @Transactional(readOnly = true)
    public Producto buscarEntidadPorId(Integer id) {
        return productoRepository.findById(id).orElseThrow(
                () -> new ProductoException(HttpStatus.NOT_FOUND, "Producto no encontrado")
        );
    }

    @Transactional(readOnly = true)
    public ProductoDto buscarPorIdDto(Integer id) {
        return productoMapper.toDTO(buscarEntidadPorId(id));
    }

    @Transactional
    public ProductoDto crear(ProductoDto dto) {
        if (dto == null || dto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoriaId()));

        Producto producto = productoMapper.toEntity(dto, categoria);
        producto = productoRepository.save(producto);
        return productoMapper.toDTO(producto);
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

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoriaId()));

        productoMapper.updateEntity(productoExistente, dto, categoria);
        productoExistente = productoRepository.save(productoExistente);
        return productoMapper.toDTO(productoExistente);
    }

    @Transactional
    public void eliminar(Integer id) {
        Producto producto = buscarEntidadPorId(id);
        productoRepository.delete(producto);
    }
}