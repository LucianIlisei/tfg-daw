package com.springboot.tfg.backend.backend.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.tfg.backend.backend.entities.User;
import com.springboot.tfg.backend.backend.repositories.UserRepository;

// Marcamos esta clase como un servicio de Spring para que pueda ser inyectada donde se necesite.
@Service
public class UserServiceImpl implements UserService {

    // Repositorio para interactuar con la base de datos de usuarios.
    private UserRepository repository;

    // Constructor para inyectar el repositorio de usuarios.
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true) // Indica que esta transacción solo es de lectura (optimiza el rendimiento).
    public Optional<User> findById(int id) {
        return repository.findById(id); // Busca un usuario por su ID en la base de datos.
    }

    @Override
    @Transactional(readOnly = false) // Indica que esta transacción puede modificar la base de datos.
    public User save(User user) {
        return repository.save(user); // Guarda o actualiza un usuario en la base de datos.
    }

    @Override
    @Transactional(readOnly = false) // Indica que esta transacción puede modificar la base de datos.
    public void deleteById(int id) {
        repository.deleteById(id); // Elimina un usuario de la base de datos según su ID.
    }
}