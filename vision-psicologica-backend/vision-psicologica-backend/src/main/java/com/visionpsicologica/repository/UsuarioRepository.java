package com.visionpsicologica.repository;

import com.visionpsicologica.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    List<UsuarioModel> findAllByNombre(String nombre);

    Optional<UsuarioModel> findByIdentificacion(String identificacion);

    Optional<UsuarioModel> findByEmail(String email);
}
