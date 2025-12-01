package com.controller;

import com.model.Usuario; 
import com.service.UsuarioService; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    
    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    /** * Verifica si el usuario autenticado tiene el rol de ADMIN. 
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(Usuario.Role.ROLE_ADMIN.name())); 
    }

    
    // NUEVO ENDPOINT: POST /api/usuarios (Registro de nuevo usuario)
    // Este endpoint debe ser accesible sin autenticación (PUBLIC)
    
    @PostMapping
    
    public ResponseEntity<?> registerUsuario(@RequestBody Usuario nuevoUsuario) {
        // 1. Verificar si el email ya existe
        if (usuarioService.findByEmail(nuevoUsuario.getEmail()) != null) {
            return new ResponseEntity<>("El email ya está registrado.", HttpStatus.CONFLICT); // 409 Conflict
        }

        // 2. Codificar la contraseña
        String encodedPassword = passwordEncoder.encode(nuevoUsuario.getPassword());
        nuevoUsuario.setPassword(encodedPassword);

        // 3. Asignar rol por defecto (ROLE_USER)
        // Se asume que tu modelo Usuario tiene un enum o campo para el rol.
        // Si tu modelo Usuario no tiene el Role, esta línea debe ser modificada o eliminada.
        try {
             nuevoUsuario.setRole(Usuario.Role.ROLE_CLIENTE);
        } catch (Exception e) {
            // Manejo de error si setRole no existe o falla (dejar solo si estás seguro del modelo)
            System.err.println("Advertencia: No se pudo establecer el rol. Asegúrate que Usuario.java tiene setRole(Usuario.Role).");
        }


        // 4. Guardar el nuevo usuario
        Usuario usuarioGuardado = usuarioService.save(nuevoUsuario);
        
        // Retornamos el usuario creado con status 201
        return new ResponseEntity<>(usuarioGuardado, HttpStatus.CREATED);
    }
    
    // Endpoint: GET /api/usuarios (Leer todos los usuarios)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // Endpoint: PUT /api/usuarios/{id} (Actualizar un usuario)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Optional<Usuario> optionalUsuario = usuarioService.findById(id);

        if (optionalUsuario.isPresent()) {
            Usuario usuarioExistente = optionalUsuario.get();
            
            
            if (usuarioDetails.getNombre() != null) {
                usuarioExistente.setNombre(usuarioDetails.getNombre());
            }
            if (usuarioDetails.getEmail() != null) {
                 usuarioExistente.setEmail(usuarioDetails.getEmail());
            }

            if (usuarioDetails.getRole() != null && isAdmin()) {
                usuarioExistente.setRole(usuarioDetails.getRole());
            }
            
            
            if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
                usuarioExistente.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
            }

            Usuario usuarioActualizado = usuarioService.save(usuarioExistente);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint: DELETE /api/usuarios/{id} (Eliminar un usuario)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}