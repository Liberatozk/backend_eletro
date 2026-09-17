package com.eletrorecicla.eletrorecicla.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Nome", length = 150, nullable = false)
    private String nome;

    @Column(name = "Email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "SenhaHash", length = 255, nullable = false)
    private String senhaHash;

    @Column(name = "Telefone", length = 20)
    private String telefone;

    @Column(name = "CPF", length = 11, unique = true)
    private String cpf;

    @Column(name = "Status", length = 10, nullable = false)
    private String status;

    @Column(name = "Role", length = 10, nullable = false)
    private String role;

    @Column(name = "DataCadastro", nullable = false)
    private LocalDateTime dataCadastro;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}