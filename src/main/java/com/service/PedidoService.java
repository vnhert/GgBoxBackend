package com.service;

import com.model.*;
import com.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

   

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    /**
     * Convierte el Carrito de Compras de un Usuario en un Pedido finalizado.
     * 
     *
     * @param userId 
     * @return 
     * @throws RuntimeException 
     */
    @Transactional
    public Pedido realizarCheckout(Long userId) {
        
        // 1. Obtener Carrito y Validar
        Carrito carrito = carritoService.getCarritoByUserId(userId);
        List<ItemCarrito> itemsCarrito = carrito.getItems();

        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío. No se puede generar un pedido.");
        }

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUserId(userId); 
        
        nuevoPedido.setTotal(carrito.getTotal()); 
        
        nuevoPedido.setEstado(Pedido.EstadoPedido.PAGADO); 
        
        
        
        for (ItemCarrito itemCarrito : itemsCarrito) {
            Producto producto = itemCarrito.getProducto();
            Integer cantidad = itemCarrito.getCantidad();

          
            if (producto.getStock() < cantidad) {
                
                throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getNombre());
            }

            
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            
            detalle.setPrecioUnitario(producto.getPrecio()); 
            
            
            nuevoPedido.addDetalle(detalle);

            
            producto.setStock(producto.getStock() - cantidad);
            
            
            productoService.updateStock(producto); 
        } 
    
      
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

       
        carrito.getItems().clear();
        
       
        carritoService.saveCarrito(carrito); 
        
        return pedidoGuardado;
    }

    /**
     * Obtiene una lista de pedidos asociados a un usuario específico.
     */
    public List<Pedido> getPedidosByUserId(Long userId) {
        return pedidoRepository.findByUserId(userId);
    }
    
    /**
     * Obtiene un pedido por su ID, lanzando una excepción si no se encuentra.
     */
    public Pedido getPedidoById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }
}