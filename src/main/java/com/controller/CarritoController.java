package com.controller;

import org.springframework.web.bind.annotation.*;
import com.model.Carrito;
import com.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
@CrossOrigin(origins = {
        "http://3.227.171.106",
        "http://localhost:5173"
})
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // GET obtener carrito de un usuario (se crea vacío si no existe)
    @GetMapping("/usuario/{usuarioId}")
    public Carrito getCarritoByUsuario(@PathVariable Long usuarioId) {
        return carritoService.getOrCreateCart(usuarioId);
    }

    // POST agregar producto al carrito
    @PostMapping("/usuario/{usuarioId}/items")
    public Carrito addItem(@PathVariable Long usuarioId,
                           @RequestParam Long productoId,
                           @RequestParam Integer cantidad) {
        return carritoService.addItem(usuarioId, productoId, cantidad);
    }

    // PUT actualizar cantidad de un item
    @PutMapping("/usuario/{usuarioId}/items/{itemId}")
    public Carrito updateItemQuantity(@PathVariable Long usuarioId,
                                      @PathVariable Long itemId,
                                      @RequestParam Integer cantidad) {
        return carritoService.updateItemQuantity(usuarioId, itemId, cantidad);
    }

    // DELETE eliminar un item del carrito
    @DeleteMapping("/usuario/{usuarioId}/items/{itemId}")
    public Carrito removeItem(@PathVariable Long usuarioId,
                              @PathVariable Long itemId) {
        return carritoService.removeItem(usuarioId, itemId);
    }

    // DELETE vaciar carrito
    @DeleteMapping("/usuario/{usuarioId}/items")
    public Carrito clearCart(@PathVariable Long usuarioId) {
        return carritoService.clearCart(usuarioId);
    }
}
