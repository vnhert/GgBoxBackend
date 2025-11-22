package com.service;

import com.model.CategoriaProducto; 
import com.model.Producto;
import com.repository.CategoriaProductoRepository; 
import com.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    
    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository; 
    
    private void validateProducto(Producto producto) {
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio debe ser un valor no negativo.");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        
        // Se mantiene la validación de categoría para el método saveProducto(Producto, Long)
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria.");
        }
    }

    @Transactional(readOnly = true)
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

  
    
   
    /**
     * Guarda un producto nuevo o actualiza uno existente, asegurando la asignación de la categoría.
     * Este es el método usado desde el controlador para CRUD completo.
     * * @param producto El objeto Producto a guardar.
     * @param categoriaId El ID de la categoría a asignar.
     * @return El Producto guardado.
     */
    @Transactional
    public Producto saveProducto(Producto producto, Long categoriaId) {
        
        // 1. Obtener y asignar la categoría
        CategoriaProducto categoria = categoriaProductoRepository.findById(categoriaId) 
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + categoriaId));
        
        producto.setCategoria(categoria);
        
        // 2. Validar (incluye la validación de la categoría que acabamos de asignar)
        validateProducto(producto);
        
        // 3. Persistir
        return productoRepository.save(producto);
    }

  
   
    /**
     * Actualiza únicamente el objeto Producto. 
     * Este método se usa desde PedidoService para actualizar el STOCK después de una compra.
     * **No necesita** la validación de categoría ni la búsqueda en el repositorio de categorías.
     * * @param producto El Producto con el stock ya modificado.
     * @return El Producto actualizado.
     */
    @Transactional
    public Producto updateStock(Producto producto) {
        // En este contexto, solo necesitamos persistir el cambio de stock.
        // Asumimos que el Producto ya es una entidad gestionada o que la validación
        // de precio/nombre no es crítica para un simple ajuste de stock.
        // Si quieres una validación mínima, puedes implementarla aquí, pero generalmente
        // el PedidoService ya validó el stock.
        
        // Simplemente guardar el producto (persistiendo el nuevo stock).
        return productoRepository.save(producto);
    }



    @Transactional
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }
}