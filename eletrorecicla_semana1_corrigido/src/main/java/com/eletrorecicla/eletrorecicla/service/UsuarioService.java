package com.eletrorecicla.eletrorecicla.service;

import com.eletrorecicla.eletrorecicla.dto.UsuarioRequest;
import com.eletrorecicla.eletrorecicla.exception.ApiException;
import com.eletrorecicla.eletrorecicla.model.entity.Usuario;
import com.eletrorecicla.eletrorecicla.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findByIdOrThrow(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public Usuario cadastrar(UsuarioRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email é obrigatório");
        }
        if (request.getSenha() == null || request.getSenha().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Senha é obrigatória");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse email");
        }
        if (request.getCpf() != null && !request.getCpf().isBlank()
                && usuarioRepository.existsByCpf(request.getCpf())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse CPF");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(request.getCpf());
        usuario.setStatus("ATIVO");
        // Autocadastro público nunca define a própria role: todo novo usuário nasce CIDADAO.
        // Promoção a ADMIN é feita manualmente no banco (ver migracao_role_admin.sql).
        usuario.setRole("CIDADAO");
        usuario.setDataCadastro(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario editar(Integer id, UsuarioRequest request) {
        Usuario usuario = findByIdOrThrow(id);

        if (request.getNome() != null && !request.getNome().isBlank()) {
            usuario.setNome(request.getNome());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // só valida duplicidade se o email estiver realmente mudando
            if (!request.getEmail().equalsIgnoreCase(usuario.getEmail())
                    && usuarioRepository.existsByEmail(request.getEmail())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse email");
            }
            usuario.setEmail(request.getEmail());
        }
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        }
        if (request.getTelefone() != null) {
            usuario.setTelefone(request.getTelefone());
        }
        if (request.getCpf() != null && !request.getCpf().isBlank()) {
            // só valida duplicidade se o CPF estiver realmente mudando
            if (!request.getCpf().equals(usuario.getCpf())
                    && usuarioRepository.existsByCpf(request.getCpf())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse CPF");
            }
            usuario.setCpf(request.getCpf());
        }
        return usuarioRepository.save(usuario);
    }

    public void inativar(Integer id) {
        Usuario usuario = findByIdOrThrow(id);
        usuario.setStatus("INATIVO");
        usuarioRepository.save(usuario);
    }
}