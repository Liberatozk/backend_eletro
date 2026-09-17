package com.eletrorecicla.eletrorecicla.service;

import com.eletrorecicla.eletrorecicla.dto.ProdutoRequest;
import com.eletrorecicla.eletrorecicla.exception.ApiException;
import com.eletrorecicla.eletrorecicla.model.entity.Produto;
import com.eletrorecicla.eletrorecicla.repository.CategoriaProdutoRepository;
import com.eletrorecicla.eletrorecicla.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaProdutoRepository categoriaProdutoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaProdutoRepository categoriaProdutoRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaProdutoRepository = categoriaProdutoRepository;
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Produto findByIdOrThrow(Integer id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    private void validarCategoriaExiste(Integer categoriaId) {
        if (categoriaId == null || !categoriaProdutoRepository.existsById(categoriaId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CategoriaId informado não existe em CategoriaProduto");
        }
    }

    public Produto cadastrar(ProdutoRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        validarCategoriaExiste(request.getCategoriaId());

        Produto produto = new Produto();
        produto.setNome(request.getNome());
        produto.setCategoriaId(request.getCategoriaId());
        produto.setPontosPorKg(request.getPontosPorKg() != null ? request.getPontosPorKg() : BigDecimal.ONE);
        produto.setDescricao(request.getDescricao());
        produto.setStatus("ATIVO");
        return produtoRepository.save(produto);
    }

    public Produto editar(Integer id, ProdutoRequest request) {
        Produto produto = findByIdOrThrow(id);

        if (request.getNome() != null && !request.getNome().isBlank()) {
            produto.setNome(request.getNome());
        }
        if (request.getCategoriaId() != null) {
            validarCategoriaExiste(request.getCategoriaId());
            produto.setCategoriaId(request.getCategoriaId());
        }
        if (request.getPontosPorKg() != null) {
            produto.setPontosPorKg(request.getPontosPorKg());
        }
        if (request.getDescricao() != null) {
            produto.setDescricao(request.getDescricao());
        }
        return produtoRepository.save(produto);
    }

    public void inativar(Integer id) {
        Produto produto = findByIdOrThrow(id);
        produto.setStatus("INATIVO");
        produtoRepository.save(produto);
    }
}
