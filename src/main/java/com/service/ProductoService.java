package com.service;

import com.model.Producto;
import com.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    private void validateProducto(Producto producto) {
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
             throw new IllegalArgumentException("El precio debe ser un valor no negativo.");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
    }

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

   
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    
    public Producto saveProducto(Producto producto) {
        validateProducto(producto);
        return productoRepository.save(producto);
    }

    
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }
}