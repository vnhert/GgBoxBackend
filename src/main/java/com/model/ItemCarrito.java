package com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "item_carrito")
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @JsonIgnore 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @Column(nullable = false)
    private Integer cantidad;
    
    
    @Transient 
    private BigDecimal subtotal; 

    public ItemCarrito() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Carrito getCarrito() { return carrito; }
    public void setCarrito(Carrito carrito) { this.carrito = carrito; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    
    public BigDecimal getSubtotal() {
        if (producto != null && producto.getPrecio() != null && cantidad != null) {
            return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }

    public void setSubtotal(BigDecimal subtotal) {
        
    }
}
