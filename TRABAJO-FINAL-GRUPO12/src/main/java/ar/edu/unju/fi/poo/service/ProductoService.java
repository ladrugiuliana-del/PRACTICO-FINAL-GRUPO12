package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import java.util.List;

public interface ProductoService {

    List<ProductoDto> listar();

    List<ProductoDto> filtrarProductos(Integer categoriaId, String marca, Double precioMin, Double precioMax);

    List<ProductoDto> buscarPorStockCritico(Integer umbral);

    ProductoDto buscarPorIdDto(Integer id);

    ProductoDto crear(ProductoDto dto);

    ProductoDto actualizar(Integer id, ProductoDto dto);

    void eliminar(Integer id);
}