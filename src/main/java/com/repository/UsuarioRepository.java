package com.repository;

import com.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {


/**
     
Busca al usuario por su nombre de usuario (el username)
Metodo crucial para el login y la validación de jwt
@param username es el nombre de usuario a buscar (debe ser unico)
@return es un objeto Optional que contiene el User si existe*/
Optional<Usuario> findByUsername(String username);
Optional<Usuario> findByEmail(String email);
}
