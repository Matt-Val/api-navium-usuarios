package com.navium.usuarios.repository;

import com.navium.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Al momento de hacer login, se necesita buscar rápidamente si el email o rut 
    // ingresado realmente existe.

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByRut(String rut);

}
