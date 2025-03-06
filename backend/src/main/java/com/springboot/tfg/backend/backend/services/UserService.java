package com.springboot.tfg.backend.backend.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.springboot.tfg.backend.backend.entities.User;

@Service
public interface UserService {

    List<User> findAll();

    Optional<User> findById(int id);

    User save(User user);

    void deleteById(int id);
}
