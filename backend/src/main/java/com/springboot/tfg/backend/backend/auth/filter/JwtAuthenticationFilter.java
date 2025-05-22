// Paquete donde se encuentra el filtro personalizado de autenticación JWT
package com.springboot.tfg.backend.backend.auth.filter;

// Importaciones necesarias para el manejo de JWT, autenticación y serialización
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.tfg.backend.backend.entities.User;
import com.springboot.tfg.backend.backend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

import static com.springboot.tfg.backend.backend.auth.filter.TokenJwtConfig.*;

// Filtro de autenticación personalizado que extiende el filtro estándar de Spring
// Se encarga de procesar el login, validar credenciales y generar el token JWT
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    // Constructor que recibe el AuthenticationManager y el UserRepository para la
    // lógica de autenticación
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    // Método que intenta autenticar al usuario leyendo el JSON del cuerpo de la
    // petición
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Mapeamos el JSON del cuerpo a un objeto User
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            // Creamos el token de autenticación con el username y password recibidos
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUserName(),
                    user.getPassword());

            // Delegamos en el AuthenticationManager para validar las credenciales
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            // Si ocurre un error al leer el cuerpo de la petición, lanzamos una excepción
            throw new RuntimeException("Error al leer las credenciales de inicio de sesión", e);
        }
    }

    // Método que se ejecuta si la autenticación fue exitosa
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult) throws IOException {
        // Obtenemos el principal autenticado (Spring Security User)
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authResult
                .getPrincipal();

        String username = userDetails.getUsername();

        // Creamos los claims personalizados que queremos incluir en el token
        Claims claims = Jwts.claims()
                .add("authorities", new ObjectMapper().writeValueAsString(userDetails.getAuthorities()))
                .add("username", username)
                .build();

        // Construimos el token JWT con claims, clave secreta y tiempo de expiración
        String jwt = Jwts.builder()
                .subject(username)
                .claims(claims)
                .signWith(SECRET_KEY)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .compact();

        // Buscamos el usuario en base de datos para obtener datos adicionales (como el
        // nombre)
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Preparamos el cuerpo de la respuesta con el token y datos del usuario
        Map<String, String> body = new HashMap<>();
        body.put("token", jwt);
        body.put("username", username);
        body.put("name", user.getName());
        body.put("message", String.format("Hola %s, has iniciado sesión con éxito", user.getName()));

        // Configuramos la respuesta HTTP con el token en el header y el cuerpo en JSON
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + jwt);
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }

    // Método que se ejecuta si la autenticación falla
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Creamos un cuerpo de error con un mensaje amigable
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error: Usuario o contraseña incorrectos.");
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}
