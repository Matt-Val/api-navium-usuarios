package com.navium.usuarios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.navium.usuarios.model.Usuario;
import com.navium.usuarios.service.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios (Registro, consulta y baja)")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en la base de datos. El email y RUT deben ser únicos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación (datos inválidos)"),
        @ApiResponse(responseCode = "409", description = "Conflicto: email/RUT duplicado")
    })
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try{ 
            // Intenta registrar un usuario
            return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(usuario));
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(DataIntegrityViolationException e) {
            // Error de unicidad (RUT o Email duplicado)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El RUT o Email ya se encuentran registrados");
        } catch(Exception e) { 
            // Si ocurre otro error, devuelve un mensaje genérico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar usuario");
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

    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza el nombre y rol de un usuario existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try{
            return ResponseEntity.ok(service.actualizar(id, usuario));
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Reactivar usuario", description = "Reactiva una cuenta de usuario desactivada (activo = true).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario reactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        try{
            service.activar(id);
            return ResponseEntity.ok("Usuario reactivado exitosamente");
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}