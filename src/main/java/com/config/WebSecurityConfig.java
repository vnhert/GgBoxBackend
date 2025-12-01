package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; 
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private UserDetailsService userService; 

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // UserService
        authProvider.setPasswordEncoder(passwordEncoder()); // BCryptPasswordEncoder
        return authProvider;
    }
    
    // =========================================================
    // CAMBIO CRUCIAL: Exponer AuthenticationManager como un Bean
    // Este método resuelve el error "could not be found".
    // =========================================================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita CSRF 
            .csrf(csrf -> csrf.disable())

            // Configuración de Sesiones: Sin estado (STATELESS) para JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Agrega el proveedor de autenticación
            .authenticationProvider(authenticationProvider()) 

            // Configuración de reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Rutas publicas (Registro, Login)
                .requestMatchers("/api/auth/**").permitAll()
                
                // Rutas publicas de lectura (permite ver los productos a cualquiera)
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() 
                
                // Rutas protegidas (requiere ADMIN para POST, PUT, DELETE)
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN") 
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ROLE_ADMIN")

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            );
        
        // Añadir el filtro JWT antes del filtro estándar
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}