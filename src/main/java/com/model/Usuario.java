package com.model;

import jakarta.persistence.*; 
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

@Entity
@Table(name = "users")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // el username tiene que ser único!!!!
    @Column(unique = true, nullable = false)
    private String username; 

    // contraseña
    @Column(nullable = false)
    private String password; 

    // restricciones de acceso a admin o user
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; 
}