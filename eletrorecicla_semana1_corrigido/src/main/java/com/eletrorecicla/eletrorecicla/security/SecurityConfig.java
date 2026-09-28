package com.eletrorecicla.eletrorecicla.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @EnableMethodSecurity habilita @PreAuthorize("hasRole('ADMIN')") nos controllers
// (ex.: EmpresaController.aprovar/reprovar). Sem essa anotação, @PreAuthorize é ignorado
// silenciosamente e o endpoint fica só "autenticado", não "autorizado por role".
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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