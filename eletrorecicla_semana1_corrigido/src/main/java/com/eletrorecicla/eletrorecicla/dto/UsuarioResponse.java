package com.eletrorecicla.eletrorecicla.dto;

import com.eletrorecicla.eletrorecicla.model.entity.Usuario;

import java.time.LocalDateTime;

/**
 * DTO de saída para Usuario. Existe só para garantir que senhaHash nunca sai da API,
 * mesmo hasheada - nunca deve ser exposta em nenhuma resposta JSON.
 */
public class UsuarioResponse {

    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String status;
    private String role;
    private LocalDateTime dataCadastro;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.id = usuario.getId();
        response.nome = usuario.getNome();
        response.email = usuario.getEmail();
        response.telefone = usuario.getTelefone();
        response.cpf = usuario.getCpf();
        response.status = usuario.getStatus();
        response.role = usuario.getRole();
        response.dataCadastro = usuario.getDataCadastro();
        return response;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}
