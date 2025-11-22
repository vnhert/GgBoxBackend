package com.repository;
import com.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Categoria, manejando el acceso a datos.
 */
@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {
    
    CategoriaProducto findByNombreIgnoreCase(String nombre);
}
