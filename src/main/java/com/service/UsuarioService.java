package com.service;


import com.model.Usuario;
import com.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     *
     * @param usuario 
     * @return 
     */
    public Usuario save(Usuario usuario) {
       
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su ID.
     * @param id 
     * @return 
     */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca un usuario por su email.
     * @param email 
     * @return 
     */
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene la lista de todos los usuarios.
     * @return 
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Elimina un usuario por su ID.
     * @param id 
     */
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
