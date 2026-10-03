package com.eletrorecicla.eletrorecicla.controller;

import com.eletrorecicla.eletrorecicla.dto.UsuarioRequest;
import com.eletrorecicla.eletrorecicla.dto.UsuarioResponse;
import com.eletrorecicla.eletrorecicla.model.entity.Usuario;
import com.eletrorecicla.eletrorecicla.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Lista de todos os usuários: só ADMIN. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> findAll() {
        return usuarioService.findAll().stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    /** Perfil do usuário logado (o frontend não precisa saber o id). */
    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        return UsuarioResponse.fromEntity(usuarioService.findByEmailOrThrow(authentication.getName()));
    }

    /** ADMIN ou o próprio usuário. */
    @GetMapping("/{id}")
    public UsuarioResponse findById(@PathVariable Integer id, Authentication authentication) {
        Usuario alvo = usuarioService.findByIdOrThrow(id);
        exigirAdminOuDono(alvo, authentication);
        return UsuarioResponse.fromEntity(alvo);
    }

    /** Cadastro público (liberado no SecurityConfig). */
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.fromEntity(usuario));
    }

    /** ADMIN ou o próprio usuário. */
    @PutMapping("/{id}")
    public UsuarioResponse editar(@PathVariable Integer id,
                                  @RequestBody UsuarioRequest request,
                                  Authentication authentication) {
        exigirAdminOuDono(usuarioService.findByIdOrThrow(id), authentication);
        return UsuarioResponse.fromEntity(usuarioService.editar(id, request));
    }

    /** ADMIN ou o próprio usuário. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Integer id, Authentication authentication) {
        exigirAdminOuDono(usuarioService.findByIdOrThrow(id), authentication);
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    /** O subject do JWT é o e-mail; compara com o e-mail do usuário-alvo. */
    private void exigirAdminOuDono(Usuario alvo, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean dono = alvo.getEmail().equalsIgnoreCase(authentication.getName());
        if (!admin && !dono) {
            throw new AccessDeniedException("Sem permissão para acessar este usuário");
        }
    }
}