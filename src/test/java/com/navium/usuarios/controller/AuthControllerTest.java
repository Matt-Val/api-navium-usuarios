package com.navium.usuarios.controller;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.repository.UsuarioRepository;
import com.navium.usuarios.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

        @Mock
        private UsuarioRepository repository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtUtil jwtUtil;

        @InjectMocks
        private AuthController controller;

        @Test
        void login_ok_retornaToken() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setEmail("test@navium.com");
                usuario.setPassword("hashed");
                usuario.setRol("ROL_OPERADOR");
                usuario.setActivo(true);
                when(repository.findByEmail("test@navium.com")).thenReturn(Optional.of(usuario));
                when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);
                when(jwtUtil.generarToken("test@navium.com", "ROL_OPERADOR")).thenReturn("token-123");
                ResponseEntity<?> response = controller.login(Map.of("email", "test@navium.com", "password", "plain"));
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("token-123", ((Map<?, ?>) response.getBody()).get("token"));
        }

        @Test
        void login_usuarioInactivo_retorna403() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setEmail("test@navium.com");
                usuario.setPassword("hashed");
                usuario.setActivo(false);
                when(repository.findByEmail("test@navium.com")).thenReturn(Optional.of(usuario));
                when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);
                ResponseEntity<?> response = controller.login(Map.of("email", "test@navium.com", "password", "plain"));
                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
                assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
        }

        @Test
        void login_credencialesInvalidas_retorna401() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setEmail("test@navium.com");
                usuario.setPassword("hashed");
                usuario.setActivo(true);
                when(repository.findByEmail("test@navium.com")).thenReturn(Optional.of(usuario));
                when(passwordEncoder.matches("plain", "hashed")).thenReturn(false);
                ResponseEntity<?> response = controller.login(Map.of("email", "test@navium.com", "password", "plain"));
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
        }

        @Test
        void login_errorInesperado_retorna500() throws Exception {
                when(repository.findByEmail("test@navium.com")).thenThrow(new RuntimeException("boom"));
                ResponseEntity<?> response = controller.login(Map.of("email", "test@navium.com", "password", "plain"));
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
        }
}
