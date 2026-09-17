package com.eletrorecicla.eletrorecicla.controller;

import com.eletrorecicla.eletrorecicla.dto.ProdutoRequest;
import com.eletrorecicla.eletrorecicla.model.entity.Produto;
import com.eletrorecicla.eletrorecicla.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<Produto> findAll() {
        return produtoService.findAll();
    }

    @GetMapping("/{id}")
    public Produto findById(@PathVariable Integer id) {
        return produtoService.findByIdOrThrow(id);
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@RequestBody ProdutoRequest request) {
        Produto produto = produtoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @PutMapping("/{id}")
    public Produto editar(@PathVariable Integer id, @RequestBody ProdutoRequest request) {
        return produtoService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Integer id) {
        produtoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
