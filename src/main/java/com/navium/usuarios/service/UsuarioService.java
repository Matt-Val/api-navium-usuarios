package com.navium.usuarios.service;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(Usuario usuario) {
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                    "Rol invalido. Valores permitidos: ROL_CENTRO_MANDO, ROL_SUCURSAL, ROL_OPERADOR"
            );
        }
        
        // Si el estado activo viene nulo (por ej. desde el BFF), lo forzamos a true
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        // Encriptamos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return repository.save(usuario);
    }

    // Método para listar solo los usuarios activos
    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    // Método para listar solo los usuarios activos
    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Método para dar de baja un usuario
    public void darDeBaja(Long id) {
        Usuario user = buscarPorId(id);
        user.setActivo(false);
        repository.save(user);
    }

    // Método para actualizar un usuario (solo nombre y rol)
    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario existing = buscarPorId(id);
        if (usuario.getNombre() != null && !usuario.getNombre().isBlank()) {
            existing.setNombre(usuario.getNombre());
        }
        if (usuario.getRol() != null) {
            existing.setRol(usuario.getRol());
        }
        return repository.save(existing);
    }

    // Método para activar un usuario dado su ID
    public void activar(Long id) {
        Usuario user = buscarPorId(id);
        user.setActivo(true);
        repository.save(user);
    }
}