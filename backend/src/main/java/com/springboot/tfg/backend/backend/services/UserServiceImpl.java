package com.springboot.tfg.backend.backend.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.springboot.tfg.backend.backend.entities.User;
import com.springboot.tfg.backend.backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
    }

}
