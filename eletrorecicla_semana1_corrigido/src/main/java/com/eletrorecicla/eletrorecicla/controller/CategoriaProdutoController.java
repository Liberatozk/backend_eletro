package com.eletrorecicla.eletrorecicla.controller;

import com.eletrorecicla.eletrorecicla.dto.CategoriaProdutoRequest;
import com.eletrorecicla.eletrorecicla.model.entity.CategoriaProduto;
import com.eletrorecicla.eletrorecicla.service.CategoriaProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaProdutoController {

    private final CategoriaProdutoService categoriaProdutoService;

    public CategoriaProdutoController(CategoriaProdutoService categoriaProdutoService) {
        this.categoriaProdutoService = categoriaProdutoService;
    }

    @GetMapping
    public List<CategoriaProduto> findAll() {
        return categoriaProdutoService.findAll();
    }

    @GetMapping("/{id}")
    public CategoriaProduto findById(@PathVariable Integer id) {
        return categoriaProdutoService.findByIdOrThrow(id);
    }

    @PostMapping
    public ResponseEntity<CategoriaProduto> cadastrar(@RequestBody CategoriaProdutoRequest request) {
        CategoriaProduto categoria = categoriaProdutoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    public CategoriaProduto editar(@PathVariable Integer id, @RequestBody CategoriaProdutoRequest request) {
        return categoriaProdutoService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        categoriaProdutoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
