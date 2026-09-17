package com.eletrorecicla.eletrorecicla.repository;

import com.eletrorecicla.eletrorecicla.model.entity.Coleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColetaRepository extends JpaRepository<Coleta, Integer> {

    List<Coleta> findByUsuarioId(Integer usuarioId);
}