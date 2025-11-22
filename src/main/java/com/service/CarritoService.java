package com.service;

import com.model.Carrito;
import com.model.ItemCarrito;
import com.model.Producto;
import com.repository.CarritoRepository;
import com.repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ProductoService productoService; 

    /**
     * Obtiene el carrito del usuario. Si no existe, crea uno nuevo.
     * @param userId 
     * @return 
     */
    public Carrito getCarritoByUserId(Long userId) {
        
        return carritoRepository.findByUserId(userId).orElseGet(() -> {
            
            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setUserId(userId);
            return carritoRepository.save(nuevoCarrito);
        });
    }

    /**
     * Añade un producto al carrito. Si el ítem ya existe, aumenta la cantidad.
     * @param userId 
     * @param productoId 
     * @param cantidad
     * @return 
     */
    @Transactional
    public Carrito addProductoToCarrito(Long userId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Carrito carrito = getCarritoByUserId(userId);
        Optional<Producto> productoOpt = productoService.getProductoById(productoId);

        if (!productoOpt.isPresent()) {
            throw new RuntimeException("Producto no encontrado con ID: " + productoId);
        }
        Producto producto = productoOpt.get();

        Optional<ItemCarrito> existingItemOpt = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId);

        if (existingItemOpt.isPresent()) {
            
            ItemCarrito existingItem = existingItemOpt.get();
            existingItem.setCantidad(existingItem.getCantidad() + cantidad);
            itemCarritoRepository.save(existingItem);
        } else {
            
            ItemCarrito newItem = new ItemCarrito();
            newItem.setProducto(producto);
            newItem.setCantidad(cantidad);
           
            carrito.addItem(newItem); 
            itemCarritoRepository.save(newItem);
        }

        return carritoRepository.save(carrito);
    }
    
    /**
     * Elimina completamente un producto del carrito.
     * @param userId
     * @param productoId 
     * @return 
     */
    @Transactional
    public Carrito removeProductoFromCarrito(Long userId, Long productoId) {
        Carrito carrito = getCarritoByUserId(userId);

        
        Optional<ItemCarrito> itemToRemoveOpt = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId);

        if (itemToRemoveOpt.isPresent()) {
            ItemCarrito itemToRemove = itemToRemoveOpt.get();
            
            
            carrito.removeItem(itemToRemove);
            itemCarritoRepository.delete(itemToRemove);
            
            return carritoRepository.save(carrito);
        } else {
            
            return carrito;
        }
    }
    
  

    /**
     * Guarda o actualiza un objeto Carrito. 
     * E
     * @param carrito 
     * @return 
     */
    @Transactional
    public Carrito saveCarrito(Carrito carrito) {
        
        return carritoRepository.save(carrito);
    }
}