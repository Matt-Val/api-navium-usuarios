package com.navium.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Crea el codificador de contrasennias
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuramos CORS antes que el resto
            .cors(Customizer.withDefaults())
            // Desactivamos el CSRF ya que la API es stateless y no usa sesiones
            .csrf(csrf -> csrf.disable())
            // Configuramos la gestión de sesiones como STATELESS.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Reglas de autorización para los endpoints
            .authorizeHttpRequests(auth -> auth
                // En este microservicio permitimos todo porque el BFF es el que valida el token
                .anyRequest().permitAll()
            );

        return http.build();
    }

        @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // IMPORTANTE: allowCredentials(true) permite que el navegador envíe cookies
        configuration.setAllowCredentials(true);
        
        // Usamos OriginPatterns con "*" para permitir CUALQUIER origen
        configuration.setAllowedOriginPatterns(List.of("*")); 
        
        // Permitimos todos los métodos (GET, POST, PUT, DELETE, OPTIONS, etc.)
        configuration.setAllowedMethods(List.of("*"));
        
        // Permitimos todos los headers
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
