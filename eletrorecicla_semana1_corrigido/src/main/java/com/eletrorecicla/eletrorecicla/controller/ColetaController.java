package com.eletrorecicla.eletrorecicla.controller;

import com.eletrorecicla.eletrorecicla.dto.ColetaRequest;
import com.eletrorecicla.eletrorecicla.dto.ColetaResponse;
import com.eletrorecicla.eletrorecicla.model.entity.Coleta;
import com.eletrorecicla.eletrorecicla.service.ColetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coletas")
public class ColetaController {

    private final ColetaService coletaService;

    public ColetaController(ColetaService coletaService) {
        this.coletaService = coletaService;
    }

    /** Lista completa — uso administrativo/depuração. Considere restringir por role antes de expor em produção. */
    @GetMapping
    public List<Coleta> findAll() {
        return coletaService.findAll();
    }

    @GetMapping("/{id}")
    public ColetaResponse buscarComItens(@PathVariable Integer id) {
        return coletaService.buscarComItens(id);
    }

    /** Histórico e pontuação do usuário logado — é isto que a tela "impacto" do frontend deve consumir. */
    @GetMapping("/me")
    public List<ColetaResponse> minhasColetas(Authentication authentication) {
        return coletaService.historicoDoUsuarioAutenticado(authentication.getName());
    }

    /**
     * Registra uma coleta em nome do usuário autenticado. O usuário NUNCA é informado pelo
     * cliente (ver ColetaRequest) — vem sempre do token, via Authentication.getName() (email).
     */
    @PostMapping
    public ResponseEntity<ColetaResponse> cadastrar(@RequestBody ColetaRequest request, Authentication authentication) {
        ColetaResponse coleta = coletaService.cadastrar(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(coleta);
    }
}
