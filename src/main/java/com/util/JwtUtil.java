package com.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_STRING; 
    
    // se dfine la validez del token en segundos 
    public static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60; 

    // Metodos de generación del token

    private Key getSigningKey() {
        // se convierte la clave secreta a un objeto key 
        byte[] keyBytes = SECRET_STRING.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Se genera un token JWT para un usuario autenticado
     * @param userDetails Los detalles del usuario (username, password, authorities)
     * @param role El rol del usuario (ADMIN o USER)
     * @return El token JWT firmado
     */
    public String generateToken(UserDetails userDetails, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); 

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) 
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Metodos de validacion y extraccion

    /**
     * Valida si un token es válido, verificando la firma y la fecha de expiración.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        // tiene que coincidir el username y el token no debe ni puede estar expirado
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * obtiene el nombre de usuario (subject) del token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * obtiene la fecha de expiración del token
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * obtiene un claim específico del token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}