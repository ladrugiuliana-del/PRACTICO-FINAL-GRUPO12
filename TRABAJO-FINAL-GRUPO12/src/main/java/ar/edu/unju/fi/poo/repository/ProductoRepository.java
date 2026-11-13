package ar.edu.unju.fi.poo.repository;

import ar.edu.unju.fi.poo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // --- METODOS anteriores mios (No se tocan avisar si es que si uwu)
    List<Producto> findByMarcaIgnoreCase(String marca);

    List<Producto> findByCategoriaId(Integer categoriaId);

    List<Producto> findByPrecioBaseBetween(double precioMin, double precioMax);

    @Query("SELECT p FROM Producto p WHERE p.stock <= :umbral")
    List<Producto> findProductosStockCritico(@Param("umbral") Integer umbral);

    // --- METODO PARA LA API REST (este seria el agregado) ---
    @Query("SELECT p FROM Producto p WHERE " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "(:marca IS NULL OR p.marca = :marca) AND " +
           "(:precioMin IS NULL OR p.precioBase >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precioBase <= :precioMax)")
    List<Producto> filtrarProductos(
            @Param("categoriaId") Integer categoriaId,
            @Param("marca") String marca,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax);
}