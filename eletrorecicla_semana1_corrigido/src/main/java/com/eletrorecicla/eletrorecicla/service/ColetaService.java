package com.eletrorecicla.eletrorecicla.service;

import com.eletrorecicla.eletrorecicla.dto.ColetaRequest;
import com.eletrorecicla.eletrorecicla.dto.ColetaResponse;
import com.eletrorecicla.eletrorecicla.dto.ItemColetaRequest;
import com.eletrorecicla.eletrorecicla.exception.ApiException;
import com.eletrorecicla.eletrorecicla.model.entity.Coleta;
import com.eletrorecicla.eletrorecicla.model.entity.Empresa;
import com.eletrorecicla.eletrorecicla.model.entity.Produto;
import com.eletrorecicla.eletrorecicla.model.entity.ProdutoColeta;
import com.eletrorecicla.eletrorecicla.model.entity.ProdutoColeta.ProdutoColetaId;
import com.eletrorecicla.eletrorecicla.model.entity.Usuario;
import com.eletrorecicla.eletrorecicla.repository.ColetaRepository;
import com.eletrorecicla.eletrorecicla.repository.EmpresaRepository;
import com.eletrorecicla.eletrorecicla.repository.ProdutoColetaRepository;
import com.eletrorecicla.eletrorecicla.repository.ProdutoRepository;
import com.eletrorecicla.eletrorecicla.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Responsável por registrar o descarte (Coleta + itens em ProdutoColeta) e calcular
 * a pontuação gerada por cada item, conforme seções 7.3 e 7.4 do documento do TCC.
 */
@Service
public class ColetaService {

    private final ColetaRepository coletaRepository;
    private final ProdutoColetaRepository produtoColetaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoRepository produtoRepository;

    public ColetaService(ColetaRepository coletaRepository,
                         ProdutoColetaRepository produtoColetaRepository,
                         UsuarioRepository usuarioRepository,
                         EmpresaRepository empresaRepository,
                         ProdutoRepository produtoRepository) {
        this.coletaRepository = coletaRepository;
        this.produtoColetaRepository = produtoColetaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Coleta> findAll() {
        return coletaRepository.findAll();
    }

    public Coleta findByIdOrThrow(Integer id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Coleta não encontrada"));
    }

    public ColetaResponse buscarComItens(Integer id) {
        Coleta coleta = findByIdOrThrow(id);
        List<ProdutoColeta> itens = produtoColetaRepository.findById_ColetaId(id);
        return ColetaResponse.from(coleta, itens);
    }

    /** Histórico e pontuação do usuário autenticado — usado pela tela "impacto" do frontend. */
    public List<ColetaResponse> historicoDoUsuarioAutenticado(String emailUsuarioAutenticado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado"));

        return coletaRepository.findByUsuarioId(usuario.getId()).stream()
                .map(coleta -> ColetaResponse.from(coleta, produtoColetaRepository.findById_ColetaId(coleta.getId())))
                .toList();
    }

    /**
     * Registra uma coleta com um ou mais itens (produto + quantidade), calculando os pontos
     * de cada item a partir de Produto.pontosPorKg. Tudo em uma única transação: se qualquer
     * item for inválido, nada é gravado.
     *
     * @param emailUsuarioAutenticado email extraído do JWT (SecurityContext) — nunca um id vindo
     *                                 do corpo da requisição, para impedir que um usuário registre
     *                                 coleta em nome de outro.
     */
    @Transactional
    public ColetaResponse cadastrar(ColetaRequest request, String emailUsuarioAutenticado) {
        if (request.getEmpresaId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "empresaId é obrigatório");
        }
        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "É necessário informar ao menos um item (produto + quantidade)");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado"));
        if (!"ATIVO".equals(usuario.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuário inativo não pode registrar coleta");
        }

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "empresaId informado não existe"));
        if (!"APROVADA".equals(empresa.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Empresa ainda não aprovada não pode registrar coletas");
        }

        long produtosDistintos = request.getItens().stream()
                .map(ItemColetaRequest::getProdutoId)
                .distinct()
                .count();
        if (produtosDistintos != request.getItens().size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Cada produto só pode aparecer uma vez por coleta — some as quantidades em um único item");
        }

        Coleta coleta = new Coleta();
        coleta.setUsuarioId(usuario.getId());
        coleta.setEmpresaId(empresa.getId());
        coleta.setDataColeta(LocalDateTime.now());
        // Coleta.Status segue o mesmo padrão de exclusão lógica de Usuario/Produto
        // (CK_Coleta_Status só aceita ATIVO/INATIVO) — não é um status de workflow.
        coleta.setStatus("ATIVO");
        coleta = coletaRepository.save(coleta);

        for (ItemColetaRequest itemRequest : request.getItens()) {
            if (itemRequest.getProdutoId() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "produtoId é obrigatório em cada item");
            }
            if (itemRequest.getQuantidadeKg() == null || itemRequest.getQuantidadeKg().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "quantidadeKg deve ser maior que zero em cada item");
            }

            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                            "produtoId " + itemRequest.getProdutoId() + " não existe"));
            if (!"ATIVO".equals(produto.getStatus())) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Produto " + produto.getId() + " está inativo e não pode ser coletado");
            }

            BigDecimal pontos = produto.getPontosPorKg().multiply(itemRequest.getQuantidadeKg())
                    .setScale(0, RoundingMode.HALF_UP);

            ProdutoColeta produtoColeta = new ProdutoColeta(
                    new ProdutoColetaId(coleta.getId(), produto.getId()),
                    itemRequest.getQuantidadeKg(),
                    pontos.intValue()
            );
            produtoColetaRepository.save(produtoColeta);
        }

        List<ProdutoColeta> itensSalvos = produtoColetaRepository.findById_ColetaId(coleta.getId());
        return ColetaResponse.from(coleta, itensSalvos);
    }
}