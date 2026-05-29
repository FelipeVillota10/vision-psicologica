package com.visionpsicologica.service;

import com.visionpsicologica.model.HistoriaClinicaModel;
import com.visionpsicologica.repository.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}