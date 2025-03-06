package com.springboot.tfg.backend.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.springboot.tfg.backend.backend.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

}
