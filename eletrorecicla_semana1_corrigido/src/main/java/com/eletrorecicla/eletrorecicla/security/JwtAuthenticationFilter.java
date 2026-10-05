package com.eletrorecicla.eletrorecicla.security;

import com.eletrorecicla.eletrorecicla.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/** Intercepta cada requisição: se vier um Bearer token válido no header Authorization
 *  e o usuário dono do token ainda estiver ATIVO, marca o usuário como autenticado para
 *  esta requisição. Se não vier token, ou o token for inválido, ou o usuário tiver sido
 *  excluído (INATIVO), apenas segue em frente sem autenticar — quem decide se a rota exige
 *  autenticação é o SecurityConfig. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.validarEExtrairClaims(token);
                String email = claims.getSubject();

                // O token pode ser válido e o usuário já ter sido excluído (soft delete):
                // só autentica se a conta ainda existir e estiver ATIVA.
                boolean ativo = usuarioRepository.findByEmail(email)
                        .map(u -> "ATIVO".equals(String.valueOf(u.getStatus())))
                        .orElse(false);

                if (ativo) {
                    String role = jwtService.extrairRole(claims);
                    List<SimpleGrantedAuthority> authorities = role != null
                            ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            : List.of();
                    var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception ex) {
                // token adulterado/expirado/inválido: não autentica.
                // A rota protegida vai devolver 401 mais adiante, no SecurityConfig.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}