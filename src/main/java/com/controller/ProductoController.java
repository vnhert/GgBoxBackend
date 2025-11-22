package com.controller;

import com.model.Producto;
import com.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    
    @GetMapping
    public List<Producto> getAllProductos() {
        return productoService.getAllProductos();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.getProductoById(id);
        
        return producto.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    /**
     * Crea un nuevo producto, esperando el ID de la Categoría como parámetro de consulta.
     * 
     */
    @PostMapping
    public ResponseEntity<?> createProducto(
            @RequestBody Producto producto,
            @RequestParam Long categoriaId) { // Recibe el ID de la Categoría
        try {
            // Llama al servicio con el producto y el ID de la categoría
            Producto nuevoProducto = productoService.saveProducto(producto, categoriaId);
            
            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED); 
        } catch (IllegalArgumentException e) {
            
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        }
    }

    
    /**
     * Actualiza un producto existente, esperando el ID de la Categoría como parámetro de consulta.
     * Si no se proporciona categoriaId, intenta usar el ID que ya tiene el producto (si viene en el body).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(
            @PathVariable Long id, 
            @RequestBody Producto productoDetails,
            @RequestParam(required = false) Long categoriaId) { // Recibe el ID de la Categoría (opcional)
        
        Optional<Producto> productoOptional = productoService.getProductoById(id);

        if (productoOptional.isPresent()) {
            Producto productoExistente = productoOptional.get();

            // 1. Actualizar campos básicos
            productoExistente.setNombre(productoDetails.getNombre());
            productoExistente.setDescripcion(productoDetails.getDescripcion());
            productoExistente.setPrecio(productoDetails.getPrecio());
            productoExistente.setStock(productoDetails.getStock());
            productoExistente.setUrlImagen(productoDetails.getUrlImagen());
            
            try {
                // 2. Determinar qué ID de Categoría usar:
                Long finalCategoriaId = categoriaId;
                
                // Si no se pasó el ID en el RequestParam, usamos el ID que ya tiene la entidad persistida.
                if (finalCategoriaId == null) {
                   if (productoExistente.getCategoria() != null) {
                       finalCategoriaId = productoExistente.getCategoria().getId();
                   } else {
                       // Si la categoría es nula y no se pasó ID, forzamos un error de validación
                       throw new IllegalArgumentException("Debe proporcionar un ID de categoría para la actualización.");
                   }
                }
                
                // 3. Guardar el producto, delegando la búsqueda de la Categoría al Service
                Producto productoActualizado = productoService.saveProducto(productoExistente, finalCategoriaId);
                return ResponseEntity.ok(productoActualizado);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
            }

        } else {
            return ResponseEntity.notFound().build(); 
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productoService.getProductoById(id).isPresent()) {
            productoService.deleteProducto(id);
            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }
}