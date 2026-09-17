package com.eletrorecicla.eletrorecicla.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ProdutoColeta")
public class ProdutoColeta {

    @EmbeddedId
    private ProdutoColetaId id;

    @Column(name = "QuantidadeKg", precision = 6, scale = 2, nullable = false)
    private BigDecimal quantidadeKg;

    @Column(name = "PontosGerados", nullable = false)
    private Integer pontosGerados;

    public ProdutoColeta() {}

    public ProdutoColeta(ProdutoColetaId id, BigDecimal quantidadeKg, Integer pontosGerados) {
        this.id = id;
        this.quantidadeKg = quantidadeKg;
        this.pontosGerados = pontosGerados;
    }

    public ProdutoColetaId getId() { return id; }
    public void setId(ProdutoColetaId id) { this.id = id; }
    public BigDecimal getQuantidadeKg() { return quantidadeKg; }
    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }
    public Integer getPontosGerados() { return pontosGerados; }
    public void setPontosGerados(Integer pontosGerados) { this.pontosGerados = pontosGerados; }

    @Embeddable
    public static class ProdutoColetaId implements Serializable {

        @Column(name = "ColetaId")
        private Integer coletaId;

        @Column(name = "ProdutoId")
        private Integer produtoId;

        public ProdutoColetaId() {}

        public ProdutoColetaId(Integer coletaId, Integer produtoId) {
            this.coletaId = coletaId;
            this.produtoId = produtoId;
        }

        public Integer getColetaId() { return coletaId; }
        public void setColetaId(Integer coletaId) { this.coletaId = coletaId; }
        public Integer getProdutoId() { return produtoId; }
        public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProdutoColetaId)) return false;
            ProdutoColetaId that = (ProdutoColetaId) o;
            return Objects.equals(coletaId, that.coletaId) && Objects.equals(produtoId, that.produtoId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coletaId, produtoId);
        }
    }
}