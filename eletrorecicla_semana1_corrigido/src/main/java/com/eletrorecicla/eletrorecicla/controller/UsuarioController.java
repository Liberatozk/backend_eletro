package com.eletrorecicla.eletrorecicla.controller;

import com.eletrorecicla.eletrorecicla.dto.UsuarioRequest;
import com.eletrorecicla.eletrorecicla.model.entity.Usuario;
import com.eletrorecicla.eletrorecicla.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public Usuario findById(@PathVariable Integer id) {
        return usuarioService.findByIdOrThrow(id);
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PutMapping("/{id}")
    public Usuario editar(@PathVariable Integer id, @RequestBody UsuarioRequest request) {
        return usuarioService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Integer id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
