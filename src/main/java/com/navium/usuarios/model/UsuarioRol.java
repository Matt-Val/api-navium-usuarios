package com.navium.usuarios.model;

import java.util.Optional;

public enum UsuarioRol {
    ROL_CENTRO_MANDO,
    ROL_SUCURSAL,
    ROL_OPERADOR;

    // Devuelve el valor canonico para usarlo como claim en el JWT.
    public String toClaim() {
        return name();
    }

    public static Optional<UsuarioRol> fromValue(String value) {
        if (value == null) {
            return Optional.empty();
        }
        // En caso de necesitar validacion y normalizacion, incluirla:
        for (UsuarioRol rol : values()) {
            if (rol.name().equals(value)) {
                return Optional.of(rol);
            }
        }
        return Optional.empty();
    }

    public static boolean esValido(String value) {
        return fromValue(value).isPresent();
    }
}
