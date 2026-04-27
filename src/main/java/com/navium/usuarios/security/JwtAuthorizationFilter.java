package com.navium.usuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extraer el encabezado Authorization
        String header = request.getHeader("Authorization");

        // Verificar si el encabezado existe y tiene el formato correcto (Bearer Token)
        if (header != null && header.startsWith("Bearer ")) {
            try {
                // Limpiamos el string para obtener solo el token encriptado
                String token = header.replace("Bearer ", "");

                // Desencriptar y validar la firma del token usando la llave secreta
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                
                // Extraer el email del usuario del token
                String username = claims.getSubject();

                // Si el usuario existe en el token, lo registramos en el contexto de Spring Security
                if (username != null) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
                }
            } catch (Exception e) {
                // Si la firma es inválida, limpiamos el contexto
                // para asegurar que la petición sea rechazada por falta de permisos.
                SecurityContextHolder.clearContext();
            }
        }
        // Permite que la petición continúe hacia el siguiente filtro.
        filterChain.doFilter(request, response);
    }
}