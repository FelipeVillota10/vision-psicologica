package com.visionpsicologica.service;

import com.visionpsicologica.model.UsuarioHistoriaId;
import com.visionpsicologica.model.UsuarioHistoriaModel;
import com.visionpsicologica.repository.UsuarioHistoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioHistoriaService {

    private final UsuarioHistoriaRepository usuarioHistoriaRepository;

    @Autowired
    public UsuarioHistoriaService(UsuarioHistoriaRepository usuarioHistoriaRepository) {
        this.usuarioHistoriaRepository = usuarioHistoriaRepository;
    }

    /**
     * HU-X Objetivo Final: Obtiene la lista de todos los psicólogos
     * que tienen acceso concedido a una historia clínica en específico.
     */
    public List<UsuarioHistoriaModel> listarUsuariosPorHistoriaId(Long idHistoria) {
        if (idHistoria == null) {
            throw new IllegalArgumentException("El ID de la historia clínica no puede ser nulo");
        }
        return usuarioHistoriaRepository.findByHistoriaClinicaId(idHistoria);
    }

    /**
     * Permite registrar una nueva asignación de acceso (vincular psicólogo a expediente)
     * validando que no se duplique la autorización en el sistema.
     */
    public UsuarioHistoriaModel asignarAcceso(UsuarioHistoriaModel usuarioHistoria) {
        // Validaciones de integridad básicas antes de procesar
        if (usuarioHistoria.getUsuario() == null || usuarioHistoria.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Se requiere un usuario válido para realizar la asignación");
        }
        if (usuarioHistoria.getHistoriaClinica() == null || usuarioHistoria.getHistoriaClinica().getId() == null) {
            throw new IllegalArgumentException("Se requiere una historia clínica válida para realizar la asignación");
        }

        // Construimos la llave compuesta manualmente para verificar si ya existe el permiso
        UsuarioHistoriaId idCompuesto = new UsuarioHistoriaId(
                usuarioHistoria.getUsuario().getId(),
                usuarioHistoria.getHistoriaClinica().getId()
        );

        if (usuarioHistoriaRepository.existsById(idCompuesto)) {
            throw new IllegalArgumentException("El usuario ya se encuentra autorizado para acceder a esta historia clínica");
        }

        return usuarioHistoriaRepository.save(usuarioHistoria);
    }

    /**
     * Revoca o elimina el permiso de un psicólogo sobre una historia clínica específica.
     */
    public void revocarAcceso(Long idUsuario, Long idHistoria) {
        UsuarioHistoriaId idCompuesto = new UsuarioHistoriaId(idUsuario, idHistoria);

        if (!usuarioHistoriaRepository.existsById(idCompuesto)) {
            throw new IllegalArgumentException("No se encontró ninguna asignación activa para los IDs provistos");
        }

        usuarioHistoriaRepository.deleteById(idCompuesto);
    }
}