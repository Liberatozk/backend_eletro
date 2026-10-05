package com.eletrorecicla.eletrorecicla.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// @EnableMethodSecurity habilita @PreAuthorize("hasRole('ADMIN')") nos controllers
// (ex.: EmpresaController.aprovar/reprovar). Sem essa anotação, @PreAuthorize é ignorado
// silenciosamente e o endpoint fica só "autenticado", não "autorizado por role".
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Origens do frontend autorizadas a chamar a API (separadas por vírgula).
    // Em produção, defina CORS_ALLOWED_ORIGINS com a URL real do site hospedado.
    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        new org.springframework.security.web.authentication.HttpStatusEntryPoint(
                                org.springframework.http.HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // /coletas/me depende do usuário autenticado (Authentication.getName() no controller);
                        // se ficasse coberto pelo permitAll de GET abaixo, authentication chegaria null lá
                        // e a requisição quebraria com 500 em vez de um 401 claro. Por isso vem antes e explícito.
                        .requestMatchers(HttpMethod.GET, "/api/v1/coletas/me").authenticated()
                        // GET de usuários expõe email/CPF/telefone de todo mundo (lista e por id) -
                        // não pode ficar coberto pelo permitAll genérico abaixo. Exige token.
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/**").authenticated()
                        // GETs continuam públicos nesta fase (não há requisito pedindo protegê-los ainda)
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        // cadastro de usuário, login e autocadastro de empresa parceira são portas de
                        // entrada públicas; a empresa nasce PENDENTE e só vira ponto oficial após
                        // aprovação administrativa (PATCH /empresas/{id}/aprovar, que exige token)
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/empresas").permitAll()
                        // qualquer outra escrita (POST/PUT/PATCH/DELETE) exige token
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}