package com.eletrorecicla.eletrorecicla.dto;

import com.eletrorecicla.eletrorecicla.model.entity.Coleta;
import com.eletrorecicla.eletrorecicla.model.entity.ProdutoColeta;

import java.util.List;

public class ColetaResponse {
    private Integer id;
    private Integer usuarioId;
    private Integer empresaId;
    private String status;
    private List<ItemColetaResponse> itens;
    private int pontosTotais;

    public static ColetaResponse from(Coleta coleta, List<ProdutoColeta> itens) {
        ColetaResponse response = new ColetaResponse();
        response.id = coleta.getId();
        response.usuarioId = coleta.getUsuarioId();
        response.empresaId = coleta.getEmpresaId();
        response.status = coleta.getStatus();
        response.itens = itens.stream().map(ItemColetaResponse::from).toList();
        response.pontosTotais = itens.stream().mapToInt(ProdutoColeta::getPontosGerados).sum();
        return response;
    }

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public Integer getEmpresaId() { return empresaId; }
    public String getStatus() { return status; }
    public List<ItemColetaResponse> getItens() { return itens; }
    public int getPontosTotais() { return pontosTotais; }

    public static class ItemColetaResponse {
        private Integer produtoId;
        private java.math.BigDecimal quantidadeKg;
        private Integer pontosGerados;

        public static ItemColetaResponse from(ProdutoColeta pc) {
            ItemColetaResponse item = new ItemColetaResponse();
            item.produtoId = pc.getId().getProdutoId();
            item.quantidadeKg = pc.getQuantidadeKg();
            item.pontosGerados = pc.getPontosGerados();
            return item;
        }

        public Integer getProdutoId() { return produtoId; }
        public java.math.BigDecimal getQuantidadeKg() { return quantidadeKg; }
        public Integer getPontosGerados() { return pontosGerados; }
    }
}
