package com.eletrorecicla.eletrorecicla.repository;

import com.eletrorecicla.eletrorecicla.model.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}