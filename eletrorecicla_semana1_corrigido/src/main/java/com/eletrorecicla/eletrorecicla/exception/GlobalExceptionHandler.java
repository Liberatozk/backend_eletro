package com.eletrorecicla.eletrorecicla.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("erro", ex.getMessage()));
    }

    /**
     * Lançada pelo @PreAuthorize("hasRole(...)") quando o usuário autenticado não tem a role
     * exigida. Sem este handler, ela cairia no catch-all genérico abaixo e viraria 500 em vez
     * de 403 — escondendo que a autorização, na verdade, funcionou corretamente.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("erro", "Você não tem permissão para executar esta ação."));
    }

    /**
     * No Spring Security 7, @PreAuthorize lança esta classe (pacote .authorization, não .access)
     * em vez da AccessDeniedException clássica. Mantemos os dois handlers porque dependendo do
     * ponto do pipeline em que a negação acontece, uma ou outra pode ser lançada.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("erro", "Você não tem permissão para executar esta ação."));
    }

    /** Ex.: excluir uma Categoria que ainda tem Produto vinculado (FK) — vira 409, não 500. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Violação de integridade no banco: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", "Não é possível concluir a operação: existem registros relacionados."));
    }

    /** Rede de segurança final: nenhuma exceção não mapeada deve virar whitelabel error page. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        log.error("Erro não tratado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro interno inesperado. Consulte os logs do servidor."));
    }
}
