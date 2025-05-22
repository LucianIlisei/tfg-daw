// Paquete donde se agrupan las configuraciones relacionadas con los tokens JWT
package com.springboot.tfg.backend.backend.auth.filter;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

// Clase de configuración estática que contiene constantes usadas en la creación y validación de tokens JWT
public class TokenJwtConfig {

    // 🔐 Clave secreta utilizada para firmar y verificar tokens JWT (debe tener una
    // longitud segura, mínimo 256 bits para HS256)
    public static final String SECRET = "Clave.Super.Secreta.Para.El.JWT.1234567890";

    // 🔑 Generamos una clave criptográfica a partir del string secreto usando el
    // método recomendado por la librería
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 📩 Nombre del header HTTP que se utiliza para enviar el token JWT
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // 🪪 Prefijo que precede al token JWT en el header (por convención se usa
    // "Bearer ")
    public static final String PREFIX_TOKEN = "Bearer ";

    // 📦 Tipo de contenido usado en las respuestas HTTP con JSON
    public static final String CONTENT_TYPE = "application/json";
}
