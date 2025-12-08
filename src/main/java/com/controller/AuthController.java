package com.controller;

import com.model.Usuario; 
import com.service.UsuarioService;
import com.util.JwtUtil; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para manejar la autenticación de usuarios (registro y login).
 *
 * NOTA: La anotación @CrossOrigin debe apuntar a la IP o dominio del frontend.
 */
@RestController
@RequestMapping("/api/auth")
<<<<<<< HEAD
@CrossOrigin(origins = "https://98.94.92.251/") 
=======
@CrossOrigin(origins = "http://localhost:3000") 
>>>>>>> a6bff63c8c0876a65c862130b162a65031d70a74
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil; // Reemplaza JwtUtil por JwtTokenProvider

    /**
     * Objeto de respuesta para el login.
     * Es crucial para que el frontend reciba el JWT, el email y el rol.
     */
    public static class AuthResponse {
        public String jwt;
        public String username; // Usamos email como username
        public String role;
        
        public AuthResponse(String jwt, String username, String role) {
            this.jwt = jwt;
            this.username = username;
            this.role = role;
        }
    }

    /**
     * Endpoint de Registro (POST /api/auth/register).
     * Registra un nuevo usuario con rol por defecto (ej. ROLE_CLIENTE).
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            // rol por defecto es USER: se maneja en UserService
            Usuario newUser = usuarioService.registrarNuevoUsuario(
                    request.get("email"),
                    request.get("username"),
                    request.get("password")
                    
                   
                    
            );
            return ResponseEntity.ok("Usuario registrado exitosamente como: " + newUser.getRole().name());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint de Login (POST /api/auth/login).
     * Autentica al usuario y genera el token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        
    

      
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.get("username"), request.get("password"))
        );
       
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); 
       
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        
      
        final String jwt = jwtUtil.generateToken(userDetails, role);

        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(), role));
    }
}

