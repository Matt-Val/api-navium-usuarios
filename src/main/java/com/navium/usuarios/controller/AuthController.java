package com.navium.usuarios.controller;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.model.UsuarioRol;
import com.navium.usuarios.repository.UsuarioRepository;
import com.navium.usuarios.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

                UsuarioRol rol = UsuarioRol.fromValue(usuario.getRol())
                    .orElseThrow(() -> new IllegalStateException("Rol invalido configurado para el usuario"));

                // Generamos el Token JWT
                String token = jwtUtil.generarToken(usuario.getEmail(), rol.toClaim());

                // Creamos la cookie HttpOnly para mayor seguridad
                ResponseCookie cookie = ResponseCookie.from("token", token)
                        .httpOnly(true)
                        .secure(false) // Cambiar a true en producción con HTTPS
                        .path("/")
                        .maxAge(10 * 60 * 60) // 10 horas (coincide con la expiración del token)
                        .sameSite("Lax")
                        .build();

                // Devolvemos el mensaje de éxito y configuramos la cookie
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(Map.of(
                            "message", "Login exitoso",
                            "user", usuario.getEmail(),
                            "rol", usuario.getRol()
                        ));
            }

            // Si estamos aquí, es porque las credenciales son incorrectas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Correo o contraseña incorrectos"));
        } catch(Exception e) { 
            e.printStackTrace();
            // Atrapamos cualquier error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error inesperado en el proceso de autenticación"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Para cerrar sesión, enviamos la cookie con maxAge 0 para que el navegador la borre
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Sesión cerrada correctamente"));
    }
}