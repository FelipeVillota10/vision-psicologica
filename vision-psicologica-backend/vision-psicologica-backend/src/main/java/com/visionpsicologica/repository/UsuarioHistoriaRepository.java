package com.visionpsicologica.repository;

import com.visionpsicologica.model.UsuarioHistoriaModel;
import com.visionpsicologica.model.UsuarioHistoriaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioHistoriaRepository extends JpaRepository<UsuarioHistoriaModel, UsuarioHistoriaId> {

    // HU-X: Recupera la lista de psicólogos (usuarios) que tienen acceso a una historia clínica específica
    List<UsuarioHistoriaModel> findByHistoriaClinicaId(Long idHistoria);

    // Método extra muy útil: Recupera todas las historias a las que un psicólogo específico tiene acceso
    List<UsuarioHistoriaModel> findByUsuarioId(Long idUsuario);
}