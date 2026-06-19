package com.visionpsicologica.repository;

import com.visionpsicologica.model.CitaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<CitaModel, Long> {


    List<CitaModel> findByPsicologoId(Long idPsicologo);

    List<CitaModel> findByClienteId(Long idCliente);
    List<CitaModel> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    @Query("SELECT c FROM CitaModel c WHERE LOWER(c.cliente.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<CitaModel> findByClienteNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT COUNT(c) > 0 FROM CitaModel c WHERE c.psicologo.id = :idPsicologo " +
            "AND (:inicio < c.fechaFin AND :fin > c.fechaInicio)")
    boolean existeCitaEnEseHorario(
            @Param("idPsicologo") Long idPsicologo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}