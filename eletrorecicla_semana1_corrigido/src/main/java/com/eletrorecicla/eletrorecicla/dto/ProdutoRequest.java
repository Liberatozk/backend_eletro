package com.eletrorecicla.eletrorecicla.dto;

import java.math.BigDecimal;

public class ProdutoRequest {
    private String nome;
    private Integer categoriaId;
    private BigDecimal pontosPorKg;
    private String descricao;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public BigDecimal getPontosPorKg() { return pontosPorKg; }
    public void setPontosPorKg(BigDecimal pontosPorKg) { this.pontosPorKg = pontosPorKg; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
