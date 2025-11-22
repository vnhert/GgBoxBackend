package com.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

/**
 * Clase de utilidad para la generación, extracción y validación de JSON Web Tokens (JWT).
 */
@Component
public class JwtUtils {

   
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

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

    /**
     * Obtiene el cuerpo (claims) del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
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

    /**
     * Método central para la creación del token.
     */
    private String createToken(Map<String, Object> claims, String userName) {
        // Establecemos un tiempo de vida del token (ej: 24 horas)
        long validityInMilliseconds = 1000 * 60 * 60 * 24; 
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds)) 
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Genera la clave de firma (Signing Key) a partir de la clave secreta base64.
     */
    private Key getSignKey() {
        // Decodifica la clave BASE64
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}