package com.eletrorecicla.eletrorecicla.service;

import com.eletrorecicla.eletrorecicla.dto.EmpresaRequest;
import com.eletrorecicla.eletrorecicla.exception.ApiException;
import com.eletrorecicla.eletrorecicla.model.entity.Empresa;
import com.eletrorecicla.eletrorecicla.repository.EmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Empresa nasce como PENDENTE (seção 7.6 do documento: cadastro não implica ativação
 * automática) e só passa a ser um ponto de coleta oficial após aprovação administrativa.
 */
@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<Empresa> findAll() {
        return empresaRepository.findAll();
    }

    public List<Empresa> findAprovadas() {
        return empresaRepository.findAll().stream()
                .filter(e -> "APROVADA".equals(e.getStatus()))
                .toList();
    }

    public Empresa findByIdOrThrow(Integer id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
    }

    public Empresa cadastrar(EmpresaRequest request) {
        if (request.getRazaoSocial() == null || request.getRazaoSocial().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "razaoSocial é obrigatória");
        }
        if (request.getCnpj() == null || request.getCnpj().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "cnpj é obrigatório");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "email é obrigatório");
        }
        if (request.getEndereco() == null || request.getEndereco().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "endereco é obrigatório");
        }
        if (empresaRepository.existsByCnpj(request.getCnpj())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe uma empresa cadastrada com esse CNPJ");
        }
        if (empresaRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Já existe uma empresa cadastrada com esse email");
        }

        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(request.getRazaoSocial());
        empresa.setCnpj(request.getCnpj());
        empresa.setEmail(request.getEmail());
        empresa.setTelefone(request.getTelefone());
        empresa.setEndereco(request.getEndereco());
        empresa.setLatitude(request.getLatitude());
        empresa.setLongitude(request.getLongitude());
        empresa.setStatus("PENDENTE");
        empresa.setDataCadastro(LocalDateTime.now());
        return empresaRepository.save(empresa);
    }

    public Empresa aprovar(Integer id) {
        Empresa empresa = findByIdOrThrow(id);
        if ("APROVADA".equals(empresa.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Empresa já está aprovada");
        }
        empresa.setStatus("APROVADA");
        empresa.setDataAprovacao(LocalDateTime.now());
        return empresaRepository.save(empresa);
    }

    public Empresa reprovar(Integer id) {
        Empresa empresa = findByIdOrThrow(id);
        empresa.setStatus("REPROVADA");
        empresa.setDataAprovacao(null);
        return empresaRepository.save(empresa);
    }
}
