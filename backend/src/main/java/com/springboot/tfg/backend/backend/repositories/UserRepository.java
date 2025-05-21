package com.springboot.tfg.backend.backend.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.tfg.backend.backend.entities.User;

// Interfaz que extiende JpaRepository para realizar operaciones CRUD en la entidad User.
public interface UserRepository extends JpaRepository<User, Integer> {

    // Método personalizado para buscar un usuario por su nombre de usuario.
    // Devuelve un Optional<User> para manejar el caso en que no se encuentre el
    // usuario.
    Optional<User> findByUserName(String name);

}