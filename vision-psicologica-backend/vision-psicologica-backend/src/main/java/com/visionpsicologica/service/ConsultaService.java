package com.visionpsicologica.service;

import com.visionpsicologica.model.ConsultaModel;
import com.visionpsicologica.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public List<ConsultaModel> obtenerTodas() {
        return consultaRepository.findAll();
    }

    public Optional<ConsultaModel> obtenerPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public List<ConsultaModel> obtenerPorHistoria(Long idHistoria) {
        return consultaRepository.findByIdHistoria(idHistoria);
    }

    public ConsultaModel guardar(ConsultaModel consulta) {
        return consultaRepository.save(consulta);
    }

    public void eliminar(Long id) {
        consultaRepository.deleteById(id);
    }
}