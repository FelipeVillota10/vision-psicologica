package com.visionpsicologica.repository;

import com.visionpsicologica.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, Long> {
    // Método útil para evitar guardar clientes duplicados
    boolean existsByIdentificacion(String identificacion);
}