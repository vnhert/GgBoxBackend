package com.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import com.model.Usuario;
import com.repository.UsuarioRepository;    
import com.security.JwtService;
import com.dto.LoginRequest;
import com.dto.LoginResponse;
import com.dto.MeResponse;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://3.227.171.106",
        "http://localhost:5173"
})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtService.generateToken(
                (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()
        );

        Usuario u = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        LoginResponse resp = new LoginResponse(
                token,
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                "ROLE_" + u.getRol().name()
        );

        return ResponseEntity.ok(resp);
    }

    // /api/auth/me (PROTEGIDO)
    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        String email = principal.getName(); // viene desde el SecurityContext (JWT)
        Usuario u = usuarioRepository.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(new MeResponse(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                "ROLE_" + u.getRol().name()
        ));
    }
}


