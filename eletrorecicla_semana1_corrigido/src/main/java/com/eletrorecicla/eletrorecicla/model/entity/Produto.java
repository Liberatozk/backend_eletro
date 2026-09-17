package com.eletrorecicla.eletrorecicla.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Nome", length = 150, nullable = false)
    private String nome;

    @Column(name = "CategoriaId", nullable = false)
    private Integer categoriaId;

    @Column(name = "PontosPorKg", precision = 5, scale = 2, nullable = false)
    private BigDecimal pontosPorKg;

    @Column(name = "Descricao", length = 255)
    private String descricao;

    @Column(name = "Status", length = 10, nullable = false)
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public BigDecimal getPontosPorKg() {
        return pontosPorKg;
    }

    public void setPontosPorKg(BigDecimal pontosPorKg) {
        this.pontosPorKg = pontosPorKg;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}