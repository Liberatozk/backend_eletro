package com.eletrorecicla.eletrorecicla.controller;

import com.eletrorecicla.eletrorecicla.dto.EmpresaRequest;
import com.eletrorecicla.eletrorecicla.model.entity.Empresa;
import com.eletrorecicla.eletrorecicla.service.EmpresaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    /** Lista completa (inclui pendentes/reprovadas) — somente ADMIN. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Empresa> findAll() {
        return empresaService.findAll();
    }

    /** Somente empresas aprovadas — é essa lista que deve alimentar o mapa do frontend (seção 7.1). */
    @GetMapping("/aprovadas")
    public List<Empresa> findAprovadas() {
        return empresaService.findAprovadas();
    }

    @GetMapping("/{id}")
    public Empresa findById(@PathVariable Integer id) {
        return empresaService.findByIdOrThrow(id);
    }

    /** Autocadastro da empresa parceira. Fica público; ela nasce PENDENTE e depende de aprovação. */
    @PostMapping
    public ResponseEntity<Empresa> cadastrar(@RequestBody EmpresaRequest request) {
        Empresa empresa = empresaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(empresa);
    }

    /** Aprovação administrativa (seção 7.7). Exige token válido E role ADMIN. */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/aprovar")
    public Empresa aprovar(@PathVariable Integer id) {
        return empresaService.aprovar(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reprovar")
    public Empresa reprovar(@PathVariable Integer id) {
        return empresaService.reprovar(id);
    }
}