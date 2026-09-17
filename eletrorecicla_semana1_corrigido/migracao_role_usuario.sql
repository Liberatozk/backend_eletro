-- Migração: adiciona a coluna Role à tabela Usuario.
-- Necessária para o JWT passar a carregar a role e o @PreAuthorize("hasRole('ADMIN')")
-- em EmpresaController.aprovar/reprovar funcionar de verdade.

ALTER TABLE Usuario
ADD Role VARCHAR(10) NOT NULL DEFAULT 'CIDADAO';

-- Restringe os valores aceitos (ajuste os nomes se usar outros papéis no futuro)
ALTER TABLE Usuario
ADD CONSTRAINT CK_Usuario_Role CHECK (Role IN ('CIDADAO', 'ADMIN'));

-- Promova manualmente o(s) usuário(s) que serão administradores.
-- Troque o email abaixo pelo do usuário real antes de rodar esta linha:
-- UPDATE Usuario SET Role = 'ADMIN' WHERE Email = 'admin@eletrorecicla.com';
