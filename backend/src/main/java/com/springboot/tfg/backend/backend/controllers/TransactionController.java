// Paquete que contiene todos los controladores del backend
package com.springboot.tfg.backend.backend.controllers;

import com.springboot.tfg.backend.backend.entities.Transaction;
import com.springboot.tfg.backend.backend.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST que devuelve JSON por defecto
@RequestMapping("/api/transactions") // Prefijo común para todas las rutas del controlador
@CrossOrigin(origins = "http://localhost:4200") // Permite llamadas desde el frontend en localhost:4200 (CORS)
public class TransactionController {

    private final TransactionService transactionService;

    // Inyectamos el servicio que se encargará de la lógica de negocio relacionada
    // con transacciones
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 🟢 Endpoint para crear una nueva transacción (POST /api/transactions)
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        // Obtenemos el nombre de usuario autenticado desde el contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        // Guardamos la transacción y la asociamos al usuario autenticado
        Transaction saved = transactionService.saveTransaction(transaction, username);

        // Respondemos con código 201 (CREATED) y el objeto guardado
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 🔵 Endpoint para obtener todas las transacciones del usuario autenticado (GET
    // /api/transactions)
    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        // Obtenemos el username del usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        // Obtenemos las transacciones asociadas a ese usuario
        List<Transaction> transactions = transactionService.getTransactions(username);

        // Respondemos con la lista en formato JSON
        return ResponseEntity.ok(transactions);
    }

}
