package com.navium.usuarios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173",
                    "http://localhost:5170",
                    "http://navium-centro-mando-app.s3-website-us-east-1.amazonaws.com",
                    "http://navium-login-central.s3-website-us-east-1.amazonaws.com",
                    "http://navium-sucursal-virtual.s3-website-us-east-1.amazonaws.com"
                    // Posteriormente se agrega la IP de Operario.
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}