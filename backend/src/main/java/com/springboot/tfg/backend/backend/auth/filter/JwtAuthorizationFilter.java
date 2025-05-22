// Paquete donde se encuentra el filtro de autorización JWT
package com.springboot.tfg.backend.backend.auth.filter;

// Importaciones necesarias para la validación del token, deserialización de claims y configuración del contexto de seguridad
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.springboot.tfg.backend.backend.auth.filter.TokenJwtConfig.*;

// Este filtro se ejecuta una vez por petición y verifica que el token JWT recibido sea válido
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // Método principal que intercepta cada request entrante
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🚫 Excluimos del filtro las rutas públicas como login y registro
        if (path.equals("/login") || path.equals("/api/users/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtenemos el header de autorización
        String header = request.getHeader(HEADER_AUTHORIZATION);

        // Si no hay header o no empieza con el prefijo esperado (ej: "Bearer "), se
        // ignora y se sigue sin autenticación
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token sin el prefijo
        String token = header.replace(PREFIX_TOKEN, "");

        try {
            // Verificamos el token con la clave secreta y extraemos los claims
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();

            // Si hay username en el token, procedemos a establecer la autenticación
            if (username != null) {
                // Extraemos los roles/authorities en formato JSON y los convertimos a objetos
                // SimpleGrantedAuthority
                String rolesJson = claims.get("authorities", String.class);
                List<Map<String, String>> rolesMap = new ObjectMapper().readValue(rolesJson, List.class);

                List<SimpleGrantedAuthority> authorities = rolesMap.stream()
                        .map(role -> new SimpleGrantedAuthority(role.get("authority")))
                        .toList();

                // Creamos el objeto de autenticación y lo configuramos con los roles extraídos
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
                        authorities);

                // Asignamos detalles adicionales desde el request (IP, etc.)
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecemos esta autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (SecurityException | IllegalArgumentException e) {
            // Si el token es inválido o ha expirado, devolvemos error 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
            return;
        }

        // Continuamos la ejecución de la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
