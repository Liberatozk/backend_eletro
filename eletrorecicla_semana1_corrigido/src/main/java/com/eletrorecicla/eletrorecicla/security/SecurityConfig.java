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
                        // IMPORTANTE: o Spring usa a PRIMEIRA regra que casar, então as mais
                        // específicas vêm antes do permitAll genérico de GET no final.

                        // Coletas: dados de histórico/pontuação de pessoas. Tudo exige token
                        // (/coletas, /coletas/me e /coletas/{id}). Quem pode ver o quê
                        // (admin vs. dono) é decidido no controller/service.
                        .requestMatchers(HttpMethod.GET, "/api/v1/coletas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/coletas/**").authenticated()

                        // Usuários: expõem email/CPF/telefone. Exige token.
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/**").authenticated()

                        // Empresas: o mapa usa só as aprovadas (público). A lista completa e a
                        // consulta por id incluem pendentes/reprovadas e exigem token
                        // (a lista completa é restrita a ADMIN via @PreAuthorize no controller).
                        .requestMatchers(HttpMethod.GET, "/api/v1/empresas/aprovadas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/empresas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/empresas/{id}").authenticated()

                        // Demais GETs (categorias, produtos) continuam públicos
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