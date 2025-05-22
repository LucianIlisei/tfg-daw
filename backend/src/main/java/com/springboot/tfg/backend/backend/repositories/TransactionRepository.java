// Paquete donde se encuentran todos los repositorios JPA del backend
package com.springboot.tfg.backend.backend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.springboot.tfg.backend.backend.entities.Transaction;
import com.springboot.tfg.backend.backend.entities.User;

// Repositorio de acceso a datos para la entidad Transaction
// Extiende CrudRepository para tener acceso a operaciones básicas: save, findById, deleteById, etc.
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    // 🔍 Método personalizado que devuelve todas las transacciones asociadas a un
    // usuario
    List<Transaction> findByUser(User user);
}
