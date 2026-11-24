package ar.edu.unju.fi.poo.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.entity.Categoria;
import ar.edu.unju.fi.poo.entity.Producto;

@Component
public class ProductoMapper {

    public ProductoDto toDTO(Producto producto) {
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

    public List<ProductoDto> toDTOList(List<Producto> productos) {
        List<ProductoDto> dtos = new ArrayList<>();
        for (Producto p : productos) {
            dtos.add(toDTO(p));
        }
        return dtos;
    }

    public Producto toEntity(ProductoDto dto, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setMarca(dto.getMarca());
        producto.setStock(dto.getStock());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setCategoria(categoria);
        return producto;
    }

    public void updateEntity(Producto productoExistente, ProductoDto dto, Categoria categoria) {
        productoExistente.setNombre(dto.getNombre());
        productoExistente.setDescripcion(dto.getDescripcion());
        productoExistente.setMarca(dto.getMarca());
        productoExistente.setStock(dto.getStock());
        productoExistente.setPrecioBase(dto.getPrecioBase());
        productoExistente.setCategoria(categoria);
    }
}
