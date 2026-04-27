package com.navium.usuarios.controller;

import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Registrar un nuevo usuario
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try{ 
            // Intenta registrar un usuario
            return ResponseEntity.ok(service.registrar(usuario));
        } catch(Exception e) { 
            // Si ocurre un error (como email o rut duplicado), devuelve un mensaje de error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar usuario: " + e.getMessage());
        }
    }


    // Consultar todos los usuarios (para administración)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try{ 
            // Intenta buscar el usuario por ID
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch(RuntimeException e) { 
            // Si no se encuentra el usuario, devuelve un mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // Dar de baja a un usuario (desactivar su cuenta)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> darDeBaja(@PathVariable Long id) {
        try{ 
            // Intenta dar de baja al usuario
            service.darDeBaja(id);
            return ResponseEntity.ok("Usuario dado de baja exitosamente");
        } catch(RuntimeException e) { 
            // Si no se encuentra el usuario, devuelve un mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}