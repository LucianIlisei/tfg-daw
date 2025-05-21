package com.springboot.tfg.backend.backend.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.springboot.tfg.backend.backend.entities.User;

// Indicamos que esta interfaz es un servicio de Spring
@Service
public interface UserService {

    // Busca un usuario por su ID y devuelve un Optional (puede estar vacío si no
    // existe)
    Optional<User> findById(int id);

    // Guarda un nuevo usuario o actualiza uno existente en la base de datos
    User save(User user);

    // Elimina un usuario de la base de datos según su ID
    void deleteById(int id);
}