package com.security.jwt;

import com.model.Usuario.Role;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer"; 
    private Long id; // ID del usuario
    private String email;
    private Role role; // Rol del usuario

    public AuthResponse(String accessToken, String tokenType, Long id, String email, Role role) {
        this.token = accessToken;
        this.type = tokenType;
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
