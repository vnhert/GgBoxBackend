package com.dto;

public class MeResponse {
    private Long id;
    private String nombre;
    private String email;
    private String role;

    public MeResponse(Long id, String nombre, String email, String role) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}