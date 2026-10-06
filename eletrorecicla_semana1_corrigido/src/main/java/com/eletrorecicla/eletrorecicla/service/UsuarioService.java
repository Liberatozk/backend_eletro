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
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

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

    public Usuario findByEmailOrThrow(String email) {
        return usuarioRepository.findByEmail(email)
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
        validarEmail(request.getEmail());
        validarSenha(request.getSenha());
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse email");
        }
        String cpf = normalizarCpf(request.getCpf());
        if (cpf != null && usuarioRepository.existsByCpf(cpf)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse CPF");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(cpf);
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
            validarEmail(request.getEmail());
            // só valida duplicidade se o email estiver realmente mudando
            if (!request.getEmail().equalsIgnoreCase(usuario.getEmail())
                    && usuarioRepository.existsByEmail(request.getEmail())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse email");
            }
            usuario.setEmail(request.getEmail());
        }
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            validarSenha(request.getSenha());
            usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        }
        if (request.getTelefone() != null) {
            usuario.setTelefone(request.getTelefone());
        }
        if (request.getCpf() != null && !request.getCpf().isBlank()) {
            String cpf = normalizarCpf(request.getCpf());
            // só valida duplicidade se o CPF estiver realmente mudando
            if (!cpf.equals(usuario.getCpf())
                    && usuarioRepository.existsByCpf(cpf)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe um usuário cadastrado com esse CPF");
            }
            usuario.setCpf(cpf);
        }
        return usuarioRepository.save(usuario);
    }

    public void inativar(Integer id) {
        Usuario usuario = findByIdOrThrow(id);
        usuario.setStatus("INATIVO");
        usuarioRepository.save(usuario);
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CPF deve ter 11 dígitos");
        }
        return digitos;
    }

    private void validarEmail(String email) {
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email inválido");
        }
    }

    private void validarSenha(String senha) {
        if (senha.length() < 6) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Senha deve ter no mínimo 6 caracteres");
        }
    }
}