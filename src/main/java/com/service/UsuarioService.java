package com.service;

import com.model.Usuario; 
import com.model.Usuario.Role; 
import com.repository.UsuarioRepository; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder; 

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmail(email);
		
		if (usuario == null) {
			throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
		}
		
		return usuario;
	}

	/**
	 * Registra un nuevo usuario, asegurando que la contraseña se guarde encriptada.
	 */
	@Transactional
	public Usuario registrarNuevoUsuario(Usuario usuario) {
		if (usuarioRepository.existsByEmail(usuario.getEmail())) {
			throw new RuntimeException("Error: El correo electrónico ya está registrado.");
		}
		
		// Encriptación de Contraseña (IE3.3.1)
		String hashedPassword = passwordEncoder.encode(usuario.getPassword());
		usuario.setPassword(hashedPassword);

		// Asignar Role por defecto si es necesario
		if (usuario.getRole() == null) {
			 usuario.setRole(Role.ROLE_CLIENTE); 
		}

		return usuarioRepository.save(usuario);
	}
	
	// --- Métodos de CRUD Estándar ---
	
	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Optional<Usuario> findById(Long id) {
		return usuarioRepository.findById(id);
	}
	
	public Usuario findByEmail(String email) {
		return usuarioRepository.findByEmail(email); 
	}

	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}

	public void deleteById(Long id) {
		usuarioRepository.deleteById(id);
	}
}