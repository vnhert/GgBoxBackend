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

    
    @PostMapping
    public ResponseEntity<?> createProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.saveProducto(producto);
            
            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED); 
        } catch (IllegalArgumentException e) {
            
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        }
    }

   
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
        Optional<Producto> productoOptional = productoService.getProductoById(id);

        if (productoOptional.isPresent()) {
            Producto productoExistente = productoOptional.get();

            
            productoExistente.setNombre(productoDetails.getNombre());
            productoExistente.setDescripcion(productoDetails.getDescripcion());
            productoExistente.setPrecio(productoDetails.getPrecio());
            productoExistente.setStock(productoDetails.getStock());
            productoExistente.setUrlImagen(productoDetails.getUrlImagen());
            
            try {
                
                Producto productoActualizado = productoService.saveProducto(productoExistente);
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