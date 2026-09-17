package com.eletrorecicla.eletrorecicla.controller;

import com.eletrorecicla.eletrorecicla.dto.LoginRequest;
import com.eletrorecicla.eletrorecicla.dto.LoginResponse;
import com.eletrorecicla.eletrorecicla.exception.ApiException;
import com.eletrorecicla.eletrorecicla.model.entity.Usuario;
import com.eletrorecicla.eletrorecicla.repository.UsuarioRepository;
import com.eletrorecicla.eletrorecicla.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getSenha() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email e senha são obrigatórios");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"));

        // Usuário inativado (soft delete) não deve conseguir logar.
        if (!"ATIVO".equals(usuario.getStatus())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario.getEmail(), usuario.getRole());
        return new LoginResponse(token);
    }
}
