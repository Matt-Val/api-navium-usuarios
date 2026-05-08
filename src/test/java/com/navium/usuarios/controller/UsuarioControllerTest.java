package com.navium.usuarios.controller;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    @Test
    void registrar_retornaOk() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@navium.com");
        usuario.setRut("11.111.111-1");
        usuario.setNombre("Test");

        when(service.registrar(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<?> response = controller.registrar(usuario);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(usuario, response.getBody());
        verify(service).registrar(any(Usuario.class));
    }

    @Test
    void registrar_cuandoError_retornaBadRequest() throws Exception {
        when(service.registrar(any(Usuario.class)))
            .thenThrow(new RuntimeException("email duplicado"));

        ResponseEntity<?> response = controller.registrar(new Usuario());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error al registrar usuario"));
    }

    @Test
    void obtenerPorId_retornaOk() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(3L);
        usuario.setEmail("test@navium.com");

        when(service.buscarPorId(3L)).thenReturn(usuario);

        ResponseEntity<?> response = controller.obtenerPorId(3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(usuario, response.getBody());
    }

    @Test
    void obtenerPorId_noEncontrado_retorna404() throws Exception {
        when(service.buscarPorId(99L))
            .thenThrow(new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> response = controller.obtenerPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    void darDeBaja_retornaOk() throws Exception {
        ResponseEntity<?> response = controller.darDeBaja(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Usuario dado de baja"));
        verify(service).darDeBaja(7L);
    }

    @Test
    void darDeBaja_noEncontrado_retorna404() throws Exception {
        doThrow(new RuntimeException("Usuario no encontrado")).when(service).darDeBaja(7L);

        ResponseEntity<?> response = controller.darDeBaja(7L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }
}
