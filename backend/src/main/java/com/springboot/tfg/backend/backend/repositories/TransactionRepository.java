package com.springboot.tfg.backend.backend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.springboot.tfg.backend.backend.entities.Transaction;
import com.springboot.tfg.backend.backend.entities.User;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}
