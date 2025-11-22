package com.controller;

import com.model.Carrito;
import com.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito/{userId}")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

   
    @GetMapping
    public Carrito getCarrito(@PathVariable Long userId) {
       
        return carritoService.getCarritoByUserId(userId);
    }

    
    @PostMapping("/add")
    public ResponseEntity<?> addProducto(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> payload) {
        
        try {
            Long productoId = ((Number) payload.get("productoId")).longValue();
            Integer cantidad = (Integer) payload.getOrDefault("cantidad", 1);
            
            Carrito carritoActualizado = carritoService.addProductoToCarrito(userId, productoId, cantidad);
            return ResponseEntity.ok(carritoActualizado);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir producto: " + e.getMessage());
        }
    }
    
    
    @DeleteMapping("/remove/{productoId}")
    public Carrito removeProducto(
            @PathVariable Long userId,
            @PathVariable Long productoId) {
        
        return carritoService.removeProductoFromCarrito(userId, productoId);
    }

    
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getCarritoTotal(@PathVariable Long userId) {
        Carrito carrito = carritoService.getCarritoByUserId(userId);
        
        return ResponseEntity.ok(Map.of(
            "userId", carrito.getUserId(),
            "total", carrito.getTotal()
        ));
    }
}