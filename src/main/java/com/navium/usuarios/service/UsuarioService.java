package com.navium.usuarios.service;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                    "Rol invalido. Valores permitidos: ROL_CENTRO_MANDO, ROL_SUCURSAL, ROL_OPERADOR"
            );
        }
        // Encriptar la contraseña con BCrypt antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void darDeBaja(Long id) {
        Usuario user = buscarPorId(id);
        user.setActivo(false);
        repository.save(user);
    }
}