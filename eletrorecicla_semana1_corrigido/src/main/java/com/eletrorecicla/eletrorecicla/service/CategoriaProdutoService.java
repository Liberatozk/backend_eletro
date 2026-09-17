package com.eletrorecicla.eletrorecicla.service;

import com.eletrorecicla.eletrorecicla.dto.CategoriaProdutoRequest;
import com.eletrorecicla.eletrorecicla.exception.ApiException;
import com.eletrorecicla.eletrorecicla.model.entity.CategoriaProduto;
import com.eletrorecicla.eletrorecicla.repository.CategoriaProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaProdutoService {

    private final CategoriaProdutoRepository categoriaProdutoRepository;

    public CategoriaProdutoService(CategoriaProdutoRepository categoriaProdutoRepository) {
        this.categoriaProdutoRepository = categoriaProdutoRepository;
    }

    public List<CategoriaProduto> findAll() {
        return categoriaProdutoRepository.findAll();
    }

    public CategoriaProduto findByIdOrThrow(Integer id) {
        return categoriaProdutoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

    public CategoriaProduto cadastrar(CategoriaProdutoRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        CategoriaProduto categoria = new CategoriaProduto();
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());
        return categoriaProdutoRepository.save(categoria);
    }

    public CategoriaProduto editar(Integer id, CategoriaProdutoRequest request) {
        CategoriaProduto categoria = findByIdOrThrow(id);
        if (request.getNome() != null && !request.getNome().isBlank()) {
            categoria.setNome(request.getNome());
        }
        if (request.getDescricao() != null) {
            categoria.setDescricao(request.getDescricao());
        }
        return categoriaProdutoRepository.save(categoria);
    }

    public void excluir(Integer id) {
        // Delete físico, como definido na tarefa: se houver Produto vinculado,
        // o próprio SQL Server barra pela FK_Produto_Categoria (erro 500 esperado por ora).
        findByIdOrThrow(id);
        categoriaProdutoRepository.deleteById(id);
    }
}
