package com.service;

import com.model.Usuario; 
import com.repository.UsuarioRepository; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencia
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscar la entidad Usuario por email. Se asume que retorna Usuario (o null).
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        // 2. Verificar si el resultado es null y lanzar la excepción de Spring Security si es necesario.
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }
        
        // 3. Retornar la entidad Usuario.
        return usuario;
    }

   
    /**
     * Guarda o actualiza un usuario en la base de datos.
     */
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su ID.
     * 
     */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email); 
    }

    /**
     * Obtiene la lista de todos los usuarios.
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Elimina un usuario por su ID.
     */
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}