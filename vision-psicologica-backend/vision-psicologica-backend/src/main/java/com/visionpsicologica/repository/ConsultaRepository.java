package com.visionpsicologica.repository;

import com.visionpsicologica.model.ConsultaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<ConsultaModel, Long> {

    List<ConsultaModel> findByIdHistoria(Long idHistoria);
}