package com.security;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// IMPORT NECESARIO PARA WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
// IMPORT NECESARIO PARA WebSecurityCustomizer
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                             AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    // =================================================================
    // SOLUCIÓN: IGNORAR FILTRO DE SEGURIDAD PARA RUTAS ESTÁTICAS DE SWAGGER
    // =================================================================
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // Ignora el filtro para todos los archivos estáticos de Swagger UI
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**"))
                // Ignora el filtro para el archivo de definición de la API (JSON)
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**"));
    }
    // =================================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://54.86.25.1",
                "http://localhost:5173"
        ));

        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        
                        // NOTA: Estas líneas son redundantes, pero se dejan para claridad.
                        // La exclusión real la hace webSecurityCustomizer
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/swagger-ui.html").permitAll()
                        
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth público SOLO login
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // /me protegido
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                        // Público: leer catálogo
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**").permitAll()

                        // ADMIN: modificar catálogo
                        .requestMatchers(HttpMethod.POST, "/api/productos/**", "/api/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**", "/api/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/productos/**", "/api/categorias/**").hasRole("ADMIN")

                        // registro público
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}