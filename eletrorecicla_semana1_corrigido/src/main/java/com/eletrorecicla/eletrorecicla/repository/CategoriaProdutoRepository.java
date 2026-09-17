package com.eletrorecicla.eletrorecicla.repository;

import com.eletrorecicla.eletrorecicla.model.entity.CategoriaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Integer> {

}