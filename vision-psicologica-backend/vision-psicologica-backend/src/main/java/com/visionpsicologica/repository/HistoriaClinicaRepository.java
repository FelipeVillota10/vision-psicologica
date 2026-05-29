package com.visionpsicologica.repository;

import com.visionpsicologica.model.HistoriaClinicaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinicaModel, Long> {

    boolean existsByClienteId(Long idCliente);

    Optional<HistoriaClinicaModel> findByClienteId(Long idCliente);
}