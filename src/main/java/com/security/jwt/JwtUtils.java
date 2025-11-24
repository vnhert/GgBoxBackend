package com.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

   
    
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;


    @Value("${jwt.expiration.ms}")
    private long JWT_TOKEN_VALIDITY; 

  
    /**
     * Extrae el nombre de usuario (subject/email) del token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Función genérica para extraer cualquier claim del cuerpo del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

   
    private Claims extractAllClaims(String token) {
       
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                
                .parseClaimsJws(token) 
                .getBody();
    }

    

    /**
     * Verifica si el token ha expirado.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida el token contra los detalles del usuario.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Genera un token JWT para un usuario.
     */
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    
    private String createToken(Map<String, Object> claims, String userName) {
        
       
        return Jwts.builder()
                
                .setSubject(userName) 
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) 
                
                .signWith(getSignKey())
                .compact(); // Finaliza la construcción y crea el token
    }
    
 
    private Key getSignKey() {
        // Decodifica la clave BASE64
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}