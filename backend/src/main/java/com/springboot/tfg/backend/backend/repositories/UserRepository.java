package com.springboot.tfg.backend.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.tfg.backend.backend.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String name);

}
