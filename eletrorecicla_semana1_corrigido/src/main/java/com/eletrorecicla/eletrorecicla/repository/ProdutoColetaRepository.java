package com.eletrorecicla.eletrorecicla.repository;

import com.eletrorecicla.eletrorecicla.model.entity.ProdutoColeta;
import com.eletrorecicla.eletrorecicla.model.entity.ProdutoColeta.ProdutoColetaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoColetaRepository extends JpaRepository<ProdutoColeta, ProdutoColetaId> {

    List<ProdutoColeta> findById_ColetaId(Integer coletaId);
}
