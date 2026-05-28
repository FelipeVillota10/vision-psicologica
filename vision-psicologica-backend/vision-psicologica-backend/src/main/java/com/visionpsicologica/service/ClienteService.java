package com.visionpsicologica.service;

import com.visionpsicologica.model.ClienteModel;
import com.visionpsicologica.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteModel guardarCliente(ClienteModel cliente) {
        // Validación adicional por si ya existe la cédula
        if (clienteRepository.existsByIdentificacion(cliente.getIdentificacion())) {
            throw new RuntimeException("Ya existe un cliente con esta identificación en el sistema");
        }

        // El guardado en la base de datos (Neon PostgreSQL)
        return clienteRepository.save(cliente);
    }
}