package com.model;


import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "categoria_producto") 
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
    
    
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Producto> productos = new HashSet<>();

  
    public CategoriaProducto() {}

    public CategoriaProducto(String nombre) {
        this.nombre = nombre;
    }

  
    public Long getId() { return id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Set<Producto> getProductos() { return productos; }
    public void setProductos(Set<Producto> productos) { this.productos = productos; }
    
   
    public void addProducto(Producto producto) {
        this.productos.add(producto);
        producto.setCategoria(this);
    }
    
   
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CategoriaProducto that = (CategoriaProducto) o; 
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
