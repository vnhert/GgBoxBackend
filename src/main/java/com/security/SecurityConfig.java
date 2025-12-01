package com.security;

import com.service.UsuarioService;
import com.security.jwt.JwtAuthenticationFilter; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración principal de Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) 
public class SecurityConfig {

    private final UsuarioService usuarioService; 
    
   
    private final AuthenticationEntryPoint unauthorizedHandler; 
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    
    public SecurityConfig(UsuarioService usuarioService, 
                          AuthenticationEntryPoint unauthorizedHandler, // Usamos la interfaz aquí
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.usuarioService = usuarioService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el proveedor de autenticación que usa el UsuarioService 
     * y el PasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
       
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
       
        authProvider.setUserDetailsService(usuarioService); 
        
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

              
                .csrf(csrf -> csrf.disable())

               
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Reglas de Autorización: Permitir acceso a la ruta de autenticación y Swagger/API Docs.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() 
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/productos/**").permitAll() // Ejemplo de ruta pública
                        .anyRequest().authenticated() // Cualquier otra ruta requiere autenticación
                );

        // 6. Integración del Proveedor y Filtro JWT
        http.authenticationProvider(authenticationProvider());
       
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    /**
     * Configuración del CORS. Permite cualquier origen, método y cabecera.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permite todos los orígenes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todas las cabeceras
        configuration.setAllowCredentials(true); 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}