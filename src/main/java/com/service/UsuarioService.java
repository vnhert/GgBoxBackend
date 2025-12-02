package com.service;
import com.model.Usuario;
import com.model.Role;
import com.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService { 

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + usernameOrEmail));
    
        
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(), // ContraseÃ±a de la DB
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRole().name()))
        );
    }

   
    public Usuario registrarNuevoUsuario(String email,String username, String rawPassword) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        Usuario newUser = new Usuario();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(Role.USER); // Rol por defecto (ajustado a la nueva estructura)

        return usuarioRepository.save(newUser);
    }

    public void createAdminUserIfNotExists() {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@hola.com");  
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN); // Rol ADMIN (ajustado a la nueva estructura)
            usuarioRepository.save(admin);
            System.out.println("Usuario ADMIN inicial creado con password 'admin123'.");
        }
    }
}