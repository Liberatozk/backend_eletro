package com.eletrorecicla.eletrorecicla.dto;

import java.util.List;

// usuarioId propositalmente NÃO existe aqui: quem registra a coleta é sempre
// o usuário autenticado no token (ver ColetaService.cadastrar), nunca um valor
// vindo do cliente — senão qualquer usuário poderia registrar coleta em nome de outro.
public class ColetaRequest {
    private Integer empresaId;
    private List<ItemColetaRequest> itens;

    public Integer getEmpresaId() { return empresaId; }
    public void setEmpresaId(Integer empresaId) { this.empresaId = empresaId; }
    public List<ItemColetaRequest> getItens() { return itens; }
    public void setItens(List<ItemColetaRequest> itens) { this.itens = itens; }
}
