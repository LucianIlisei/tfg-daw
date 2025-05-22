// Paquete que contiene los servicios del backend (capa de lógica de negocio)
package com.springboot.tfg.backend.backend.services;

import com.springboot.tfg.backend.backend.entities.Transaction;
import com.springboot.tfg.backend.backend.entities.User;
import com.springboot.tfg.backend.backend.repositories.TransactionRepository;
import com.springboot.tfg.backend.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Marca esta clase como un servicio de Spring, para que pueda ser inyectado
         // donde se necesite
public class TransactionService {

    // Inyectamos el repositorio de transacciones
    @Autowired
    private TransactionRepository transactionRepository;

    // Inyectamos el repositorio de usuarios
    @Autowired
    private UserRepository userRepository;

    // 💾 Guarda una transacción asociándola al usuario autenticado
    public Transaction saveTransaction(Transaction transaction, String username) {
        // Buscamos el usuario a partir del username (lo obtenemos desde el token JWT)
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asociamos la transacción al usuario
        transaction.setUser(user);

        // Guardamos la transacción en la base de datos
        return transactionRepository.save(transaction);
    }

    // 🔍 Devuelve la lista de transacciones del usuario autenticado
    public List<Transaction> getTransactions(String username) {
        // Buscamos el usuario autenticado
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtenemos todas las transacciones asociadas a ese usuario
        return transactionRepository.findByUser(user);
    }
}
