// Paquete donde están definidas las entidades del modelo de datos
package com.springboot.tfg.backend.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

// 📦 Entidad que representa una transacción financiera (ingreso o gasto)
@Entity
@Table(name = "transactions") // Mapeamos esta entidad a la tabla "transactions" en la base de datos
public class Transaction {

    // 🔑 ID único autogenerado para cada transacción
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💰 Cantidad de dinero involucrada en la transacción (obligatoria y positiva)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser positiva")
    private double amount;

    // 📈 Tipo de transacción: puede ser "INGRESO" o "GASTO" (obligatorio)
    @NotBlank(message = "El tipo de transacción es obligatorio")
    private String type;

    // 📝 Descripción opcional de la transacción
    private String description;

    // 🕒 Fecha y hora de creación de la transacción (se asigna automáticamente al
    // momento de instanciar)
    private LocalDateTime date = LocalDateTime.now();

    // 🔗 Relación muchos-a-uno con el usuario que realiza la transacción
    @ManyToOne(fetch = FetchType.LAZY) // Carga perezosa para optimizar rendimiento
    @JoinColumn(name = "user_id") // La columna "user_id" enlaza con la tabla de usuarios
    private User user;

    // === Getters y setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
