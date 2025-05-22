// Paquete donde están todos los controladores REST del proyecto
package com.springboot.tfg.backend.backend.controllers;

// Importaciones necesarias para manejar peticiones, validaciones y respuestas
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.tfg.backend.backend.entities.User;
import com.springboot.tfg.backend.backend.services.UserService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@CrossOrigin(origins = "http://localhost:4200") // Permite que el frontend en Angular acceda a este backend
@RestController // Declara esta clase como un controlador REST que devuelve respuestas JSON
@RequestMapping("/api/users") // Ruta base para todas las operaciones relacionadas con usuarios
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService service;

    // Constructor con inyección de dependencias
    public UserController(UserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔎 GET /api/users/{id} — Devuelve un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> showById(@PathVariable int id) {
        Optional<User> userOptional = service.findById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok().body(userOptional.get()); // Usuario encontrado
        }
        // Usuario no encontrado: devolvemos 404 con mensaje personalizado
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "El usuario no se ha encontrado con el id: " + id));
    }

    // 📝 POST /api/users/register — Registra un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
        // Si hay errores de validación, respondemos con los errores
        if (result.hasErrors()) {
            return validator(result);
        }

        // Encriptamos la contraseña antes de guardar el usuario
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Guardamos y devolvemos el usuario creado con estado 201
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    // 🔧 Método auxiliar para formatear errores de validación en un mapa
    // clave-valor
    private ResponseEntity<?> validator(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "Error en " + error.getField() + ": " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors); // Código 400 + errores
    }
}
