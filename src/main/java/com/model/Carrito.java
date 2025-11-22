package com.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @Column(name = "user_id", unique = true) 
    private Long userId; 

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Transient // No persistido en DB
    private BigDecimal total;

  
    public Carrito() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<ItemCarrito> getItems() { return items; }
    public void setItems(List<ItemCarrito> items) { this.items = items; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

   
    public BigDecimal getTotal() {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void setTotal(BigDecimal total) {
       
    }

    
    public void addItem(ItemCarrito item) {
        items.add(item);
        item.setCarrito(this);
    }

    /**
     * Elimina un ItemCarrito de la lista.
     */
    public void removeItem(ItemCarrito item) {
        items.remove(item);
        item.setCarrito(null);
    }
}
