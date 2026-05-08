package com.navium.usuarios.controller;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.repository.UsuarioRepository;
import com.navium.usuarios.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try{ 
            String email = credenciales.get("email");
            String password = credenciales.get("password");

            Optional<Usuario> userOpt = repository.findByEmail(email);

            // Verificamos si el usuario existe y si la contraseña es correcta
            if(userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) { 
                Usuario usuario = userOpt.get();

                // Verificamos si el usuario está activo
                if(!usuario.getActivo()) { 
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "La cuenta se encuentra desactivada"));
                }

                // Generamos el Token JWT
                String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());

                // Devolvemos el token en la respuesta
                return ResponseEntity.ok(Map.of("token", token));
            }

            // Si estamos aquí, es porque las credenciales son incorrectas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Correo o contraseña incorrectos"));
        } catch(Exception e) { 
            e.printStackTrace();
            // Atrapamos cualquier error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error inesperado en el proceso de autenticación"));
        }
    }
}