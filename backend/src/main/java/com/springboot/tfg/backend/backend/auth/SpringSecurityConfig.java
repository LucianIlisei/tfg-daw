// Paquete donde se encuentra esta configuración de seguridad
package com.springboot.tfg.backend.backend.auth;

// Importaciones necesarias para la configuración de seguridad y filtros personalizados
import com.springboot.tfg.backend.backend.auth.filter.JwtAuthenticationFilter;
import com.springboot.tfg.backend.backend.auth.filter.JwtAuthorizationFilter;
import com.springboot.tfg.backend.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Marca esta clase como una clase de configuración de Spring (para ser
               // detectada automáticamente)
public class SpringSecurityConfig {

    // Inyectamos el objeto AuthenticationConfiguration que nos permite construir el
    // AuthenticationManager
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    // Inyectamos el repositorio de usuarios para usarlo en el filtro de
    // autenticación JWT
    @Autowired
    private UserRepository userRepository;

    // Bean que expone el AuthenticationManager necesario para autenticar usuarios
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Bean que define el encoder de contraseñas: se utiliza BCrypt para almacenar
    // las contraseñas de forma segura
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración principal de la cadena de seguridad (SecurityFilterChain)
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Se instancia el filtro de autenticación JWT, y se le indica la URL para
        // procesar login
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authenticationManager(), userRepository);
        authFilter.setFilterProcessesUrl("/login");

        return http
                // Desactivamos CSRF ya que se trabaja con JWT y no con sesiones
                .csrf(csrf -> csrf.disable())

                // Configuramos CORS para permitir peticiones desde el frontend (Angular en este
                // caso)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Establecemos la política de sesiones como stateless, ya que JWT no usa
                // sesiones del servidor
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Permitimos todas las peticiones sin autenticación (puedes ajustar esto según
                // las rutas protegidas)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())

                // Añadimos el filtro de autenticación JWT antes del filtro estándar de Spring
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)

                // Añadimos el filtro de autorización JWT para validar el token en cada petición
                .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)

                // Construimos la configuración
                .build();
    }

    // Configuración de CORS para permitir la comunicación entre el frontend y
    // backend
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // Permitimos origen desde Angular
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos permitidos
        config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Headers que aceptamos
        config.setExposedHeaders(List.of("Authorization")); // Headers visibles en la respuesta
        config.setAllowCredentials(true); // Permitimos el envío de credenciales (cookies, headers, etc.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplicamos esta config a todas las rutas
        return source;
    }
}
