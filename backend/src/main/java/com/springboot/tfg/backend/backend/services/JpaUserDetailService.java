package com.springboot.tfg.backend.backend.services;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.tfg.backend.backend.entities.User;
import com.springboot.tfg.backend.backend.repositories.UserRepository;

// Marcamos esta clase como un servicio de Spring para que sea detectada e inyectada automáticamente.
@Service
public class JpaUserDetailService implements UserDetailsService {

    // Repositorio para acceder a la base de datos y obtener los usuarios.
    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = true) // Indica que esta operación solo realiza lectura en la base de datos.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Busca un usuario en la base de datos por su nombre de usuario.
        Optional<User> userOptional = repository.findByUserName(username);

        // Si el usuario no existe, lanza una excepción indicando que no se encontró.
        if (userOptional.isPresent()) {
            // Obtiene el usuario de la base de datos.
            User user = userOptional.get();

            // Retorna un objeto UserDetails de Spring Security con los datos del
            // usuario.
            return new org.springframework.security.core.userdetails.User(
                    username, // Nombre de usuario para autenticación.
                    user.getPassword(), // Contraseña encriptada (Spring Security la compara con la ingresada en el
                                        // login).
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())) // Asigna el rol
                                                                                                    // correctamente.
            );

        }
        throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema", username));
    }

}