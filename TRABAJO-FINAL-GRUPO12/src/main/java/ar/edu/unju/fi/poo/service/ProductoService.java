package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.ProductoDto;
import ar.edu.unju.fi.poo.entity.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> listarProductos();
    Optional<Producto> buscarPorId(Integer id);
    Producto guardarProducto(Producto producto);
    List<Producto> filtrarPorMarca(String marca);
    List<Producto> filtrarPorCategoria(Integer categoriaId);
    List<Producto> filtrarPorRangoPrecio(double min, double max);
    List<Producto> obtenerStockCritico(Integer umbral);
    void gestionarStock(Integer productoId, int cantidadVendida);

    List<ProductoDto> listar();
    List<ProductoDto> filtrarProductos(Integer categoriaId, String marca, Double precioMin, Double precioMax);
    List<ProductoDto> buscarPorStockCritico(Integer umbral);
    Producto buscarEntidadPorId(Integer id);
    ProductoDto buscarPorIdDto(Integer id);
    ProductoDto crear(ProductoDto dto);
    ProductoDto actualizar(Integer id, ProductoDto dto);
    void eliminar(Integer id);
}