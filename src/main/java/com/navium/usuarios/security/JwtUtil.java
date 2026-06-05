package com.navium.usuarios.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String generarToken(String email, String rol) {
        return Jwts.builder()
                // Identidad principal del token
                .setSubject(email)
                // Claims adicionales: Agregamos rol del usuario
                .claim("rol", rol)
                // Compatibilidad con librería de autorización
                .claim("roles", List.of(rol))
                // Fecha de emisión del token (Momento exacto del login)
                .setIssuedAt(new Date())
                // Fecha de expiración del token (10 horas desde la emisión)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                // Firma usando el HASH y la llave secreta configurada en application.properties
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                // Construye y serializa el token en un string compacto para la web
                .compact();
    }
}