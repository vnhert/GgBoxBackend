package com.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.model.Carrito;
import com.model.ItemCarrito;
import com.model.Producto;
import com.model.Usuario;
import com.repository.ItemCarritoRepository;
import java.util.Optional;
import com.repository.CarritoRepository;
import com.repository.ProductoRepository;
import com.repository.UsuarioRepository;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          ItemCarritoRepository carritoItemRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    // obtener el carrito de un usuario (si no existe, se crea vacio)
    public Carrito getOrCreateCart(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    return carritoRepository.save(nuevo);
                });
    }

    // agregar producto al carrito
    public Carrito addItem(Long usuarioId, Long productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor a 0");
        }

        Carrito carrito = getOrCreateCart(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // valida si existe item con ese producto
        Optional<ItemCarrito> existenteOpt = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst();

        if (existenteOpt.isPresent()) {
            ItemCarrito existente = existenteOpt.get();
            existente.setCantidad(existente.getCantidad() + cantidad);
            carritoItemRepository.save(existente);
        } else {
            ItemCarrito item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(cantidad);
            carrito.getItems().add(item);
            carritoItemRepository.save(item);
        }

        return carritoRepository.save(carrito);
    }

    public Carrito updateItemQuantity(Long usuarioId, Long itemId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor a 0");
        }

        Carrito carrito = getOrCreateCart(usuarioId);

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item no encontrado en el carrito"));

        item.setCantidad(cantidad);
        carritoItemRepository.save(item);

        return carrito;
    }

    public Carrito removeItem(Long usuarioId, Long itemId) {
        Carrito carrito = getOrCreateCart(usuarioId);

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item no encontrado en el carrito"));

        carrito.getItems().remove(item);
        carritoItemRepository.delete(item);

        return carrito;
    }

    public Carrito clearCart(Long usuarioId) {
        Carrito carrito = getOrCreateCart(usuarioId);
        carritoItemRepository.deleteAll(carrito.getItems());
        carrito.getItems().clear();
        return carritoRepository.save(carrito);
    }
}