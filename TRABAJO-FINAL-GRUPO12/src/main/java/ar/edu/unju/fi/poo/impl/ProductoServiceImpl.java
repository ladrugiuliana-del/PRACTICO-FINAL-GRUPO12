package ar.edu.unju.fi.poo.impl;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.entity.Categoria;
import ar.edu.unju.fi.poo.entity.Producto;
import ar.edu.unju.fi.poo.exceptions.ProductoException;
import ar.edu.unju.fi.poo.mapper.ProductoMapper;
import ar.edu.unju.fi.poo.repository.CategoriaRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.service.ProductoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> listar() {
        return productoMapper.toDTOList(productoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> filtrarProductos(Integer categoriaId, String marca, Double precioMin, Double precioMax) {
        return productoMapper.toDTOList(productoRepository.filtrarProductos(categoriaId, marca, precioMin, precioMax));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarPorStockCritico(Integer umbral) {
        return productoMapper.toDTOList(productoRepository.findProductosStockCritico(umbral));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto buscarPorIdDto(Integer id) {
        return productoMapper.toDTO(buscarEntidadPorId(id));
    }

    @Override
    @Transactional
    public ProductoDto crear(ProductoDto dto) {
        validarProductoDto(dto);

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoriaId()
                ));

        Producto producto = productoMapper.toEntity(dto, categoria);
        producto = productoRepository.save(producto);
        return productoMapper.toDTO(producto);
    }

    @Override
    @Transactional
    public ProductoDto actualizar(Integer id, ProductoDto dto) {
        validarProductoDto(dto);

        Producto productoExistente = buscarEntidadPorId(id);

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ProductoException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoriaId()
                ));

        productoMapper.updateEntity(productoExistente, dto, categoria);
        productoExistente = productoRepository.save(productoExistente);
        return productoMapper.toDTO(productoExistente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Producto producto = buscarEntidadPorId(id);
        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarEntidadPorId(Integer id) {
        return productoRepository.findById(id).orElseThrow(
                () -> new ProductoException(HttpStatus.NOT_FOUND, "Producto no encontrado")
        );
    }

    @Transactional
    public void gestionarStock(Integer productoId, int cantidadVendida) {
        Producto producto = buscarEntidadPorId(productoId);
        producto.actualizarStock(cantidadVendida);
        productoRepository.save(producto);
    }

    private void validarProductoDto(ProductoDto dto) {
        if (dto == null || dto.getStock() == null || dto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
    }
}