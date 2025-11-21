package com.repository;

import com.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Método para buscar productos por nombre, ignorando mayúsculas/minúsculas.
     
     @param nombre 
     * @return
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
