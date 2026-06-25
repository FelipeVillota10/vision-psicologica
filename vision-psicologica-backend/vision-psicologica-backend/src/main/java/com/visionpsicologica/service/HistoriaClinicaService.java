package com.visionpsicologica.service;

import com.visionpsicologica.model.HistoriaClinicaModel;
import com.visionpsicologica.repository.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;

    public HistoriaClinicaModel guardarHistoriaClinica(HistoriaClinicaModel historia) {

        if (historia.getCliente() == null || historia.getCliente().getId() == null) {
            throw new RuntimeException("No se puede crear una historia clínica sin asociar un cliente válido");
        }

        Long idCliente = historia.getCliente().getId();
        if (historiaClinicaRepository.existsByClienteId(idCliente)) {
            throw new RuntimeException("El cliente seleccionado ya cuenta con una historia clínica activa en el sistema");
        }

        return historiaClinicaRepository.save(historia);
    }


    public Optional<HistoriaClinicaModel> obtenerPorClienteId(Long idCliente) {
        return historiaClinicaRepository.findByClienteId(idCliente);
    }

    public List<HistoriaClinicaModel> obtenerTodas() {
        return historiaClinicaRepository.findAll();
    }

    public Optional<HistoriaClinicaModel> buscarPorId(Long id) {
        return historiaClinicaRepository.findById(id);
    }
    public Optional<HistoriaClinicaModel> actualizar(Long id, HistoriaClinicaModel historiaModificada) {
        // 1. Verificamos si la historia realmente existe en la base de datos
        if (!historiaClinicaRepository.existsById(id)) {
            return Optional.empty();
        }

        // 2. Nos aseguramos de que el ID del objeto sea el mismo de la URL
        historiaModificada.setId(id);

        // 3. Guardamos los cambios (Spring Data JPA hace un UPDATE si el ID ya existe)
        return Optional.of(historiaClinicaRepository.save(historiaModificada));
    }
}