package com.springboot.tfg.backend.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representa a un usuario en la base de datos.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"), // Garantiza que el email sea único
        @UniqueConstraint(columnNames = "user_name") // Garantiza que el nombre de usuario sea único
})
public class User {

    // 🔹 Identificador único para cada usuario (autoincrementado)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 🔹 Nombre de usuario (obligatorio, entre 5 y 12 caracteres)
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 5, max = 12, message = "El nombre de usuario debe tener de 5 a 12 caracteres")
    private String userName;

    // 🔹 Email (obligatorio, debe ser un formato válido)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    // 🔹 Contraseña (obligatoria, mínimo 5 caracteres)
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 5, message = "La contraseña debe tener como mínimo 5 caracteres")
    private String password;

    // 🔹 Nombre completo (obligatorio)
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    // 🔹 Balance total del usuario (por defecto 0.0 en la base de datos)
    private double totalBalance;

    // 🔹 Rol del usuario (por defecto "USER")
    private String role = "USER";

    // 🔹 Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
