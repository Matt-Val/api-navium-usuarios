package com.navium.usuarios.config;

import com.navium.usuarios.security.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Crea el codificador de contrasennias
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthorizationFilter jwtFilter) throws Exception {
        http
            // Desactivamos el CSRF ya que la API es stateless y no usa sesiones
            .csrf(csrf -> csrf.disable())
            // Configuramos la gestión de sesiones como STATELESS.
            // El servidor no guardará el estado del usuario, cada petición debe enviar el token JWT.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Reglas de autorización para los endpoints
            .authorizeHttpRequests(auth -> auth
                // Rutas Públicas, se permite el acceso para poder registrarse y obtener el token
                .requestMatchers("/api/auth/login", "/api/usuarios", "swagger-ui/**", "v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Rutas Protegidas, cualquier otra petición requiere que el usuario esté autenticado
                .anyRequest().authenticated()
            )

            // Colocamos el filtro JWT antes del filtro de autenticación de Spring Security
            // Asegura que el token JWT se valide antes de que Spring Security intente autenticar la solicitud
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
