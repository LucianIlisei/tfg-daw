package com.springboot.tfg.backend.backend.auth.filter;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class TokenJwtConfig {
    public static final String SECRET = "Clave.Super.Secreta.Para.El.JWT.1234567890";
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String CONTENT_TYPE = "application/json";
}
