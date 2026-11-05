package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Filtrar por marca
    List<Producto> findByMarcaIgnoreCase(String marca);

    // Filtrar por categoría
    List<Producto> findByCategoriaId(Integer categoriaId);

    // Filtrar por rangos de precio
    List<Producto> findByPrecioBaseBetween(double precioMin, double precioMax);

    // Consultar productos con stock crítico o por debajo de un umbral
    @Query("SELECT p FROM Producto p WHERE p.stock <= :umbral")
    List<Producto> findProductosStockCritico(@Param("umbral") Integer umbral);
}
