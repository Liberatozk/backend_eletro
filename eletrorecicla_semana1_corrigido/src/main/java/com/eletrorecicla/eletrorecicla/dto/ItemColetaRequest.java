package com.eletrorecicla.eletrorecicla.dto;

import java.math.BigDecimal;

public class ItemColetaRequest {
    private Integer produtoId;
    private BigDecimal quantidadeKg;

    public Integer getProdutoId() { return produtoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }
    public BigDecimal getQuantidadeKg() { return quantidadeKg; }
    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }
}
