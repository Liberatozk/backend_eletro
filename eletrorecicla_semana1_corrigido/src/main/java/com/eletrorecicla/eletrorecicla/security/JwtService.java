package com.eletrorecicla.eletrorecicla.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** Gera e valida os tokens JWT, incluindo a role do usuário como claim customizado. */
@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String gerarToken(String email, String role) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ROLE, role)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(secretKey)
                .compact();
    }

    /** Valida a assinatura/expiração e devolve as claims. Lança exceção do jjwt se inválido/expirado. */
    public Claims validarEExtrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Retorna o email (subject) se o token for válido, ou lança exceção do jjwt se inválido/expirado. */
    public String validarEExtrairEmail(String token) {
        return validarEExtrairClaims(token).getSubject();
    }

    public String extrairRole(Claims claims) {
        return claims.get(CLAIM_ROLE, String.class);
    }
}
