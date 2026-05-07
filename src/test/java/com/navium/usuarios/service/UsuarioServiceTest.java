package com.navium.usuarios.service;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void registrar_encriptaPassword_yGuardaUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@navium.com");
        usuario.setPassword("plain");

        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario resultado = service.registrar(usuario);

        assertEquals("hashed", resultado.getPassword());
        verify(passwordEncoder).encode("plain");
        verify(repository).save(usuario);
    }

    @Test
    void listarTodos_retornaLista() {
        when(repository.findAll()).thenReturn(List.of(new Usuario(), new Usuario()));

        List<Usuario> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaExcepcion() {
        when(repository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.buscarPorId(10L));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void darDeBaja_marcaInactivo_yGuarda() {
        Usuario usuario = new Usuario();
        usuario.setId(5L);
        usuario.setActivo(true);

        when(repository.findById(5L)).thenReturn(Optional.of(usuario));
        when(repository.save(usuario)).thenReturn(usuario);

        service.darDeBaja(5L);

        assertFalse(usuario.getActivo());
        verify(repository).save(usuario);
    }
}
