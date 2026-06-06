package com.navium.usuarios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios (Registro, consulta y baja)")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en la base de datos. El email y RUT deben ser únicos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación (email/RUT duplicado o datos inválidos)")
    })
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try{ 
            // Intenta registrar un usuario
            return ResponseEntity.ok(service.registrar(usuario));
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e) { 
            // Si ocurre un error (como email o rut duplicado), devuelve un mensaje de error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar usuario: " + e.getMessage());
        }
    }


    @Operation(summary = "Obtener usuario por ID", description = "Busca la información detallada de un usuario específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
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


    @Operation(summary = "Dar de baja a un usuario", description = "Desactiva la cuenta de un usuario (cambia el estado activo a false).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
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