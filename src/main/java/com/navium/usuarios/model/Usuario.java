package com.navium.usuarios.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name= "rut", unique = true, nullable = false)
    private String rut;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "rol")
    @Enumerated(EnumType.STRING)
    private UsuarioRol rol;

    @Column(name = "activo")
    private Boolean activo = true;
    
}