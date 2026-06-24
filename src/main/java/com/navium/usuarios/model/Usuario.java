package com.navium.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios", schema = "navium")
@Schema(description = "Modelo que representa a un usuario en el sistema Navium")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Identificador único autoincremental", example = "1")
    private Long id;

    @Column(name= "rut", unique = true, nullable = false)
    @Schema(description = "RUT del usuario (único)", example = "12.345.678-9")
    private String rut;

    @Column(name = "nombre", nullable = false)
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @Column(name = "email", unique = true, nullable = false)
    @Schema(description = "Correo electrónico (usado para login)", example = "juan.perez@navium.com")
    private String email;

    @Column(name = "password", nullable = false)
    @Schema(description = "Contraseña del usuario (se guarda encriptada)", accessMode = Schema.AccessMode.WRITE_ONLY, example = "miPasswordSecreto")
    private String password;

    @Column(name = "rol")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Rol asignado al usuario", example = "ADMIN")
    private UsuarioRol rol;

    @Column(name = "activo")
    @Schema(description = "Estado de la cuenta (activo/desactivado)", example = "true")
    private Boolean activo = true;
    
}