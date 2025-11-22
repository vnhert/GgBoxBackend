package com.model;

import jakarta.persistence.*; 
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, unique = true) 
    private String email;

    @Column(nullable = false) 
    private String password;

    @Column(nullable = false) 
    private String nombre;

    private String apellido; 
    
    // Campo ESENCIAL para la seguridad
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; 

    // Enum para definir los roles disponibles
    public enum Role {
        ROLE_CLIENTE,
        ROLE_ADMIN
    }

   
    public Usuario() {
    }

    public Usuario(String email, String password, String nombre, String apellido, Role role) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.role = role;
    }
    
    // Getters y Setters de los campos que ya tenías
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombre() { return nombre; } // YA ESTÁ AQUÍ, AHORA FUNCIONARÁ
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    // Getters y Setters para el campo Role (ESENCIAL)
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email; // Usamos el email como nombre de usuario para el login
    }

    // Devolvemos true para todas estas implementaciones por defecto
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
