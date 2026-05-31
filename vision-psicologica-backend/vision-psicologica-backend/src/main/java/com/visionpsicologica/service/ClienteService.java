package com.visionpsicologica.service;

import com.visionpsicologica.model.ClienteModel;
import com.visionpsicologica.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<ClienteModel> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    @Transactional
    public ClienteModel actualizarCliente(Long id, ClienteModel clienteDetalles) {
        ClienteModel clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        // Validar si la identificación cambió y si el nuevo valor ya existe en otro registro
        if (!clienteExistente.getIdentificacion().equals(clienteDetalles.getIdentificacion())) {
            if (clienteRepository.existsByIdentificacion(clienteDetalles.getIdentificacion())) {
                throw new RuntimeException("Ya existe otro cliente con la identificación: " + clienteDetalles.getIdentificacion());
            }
        }

        // Actualizamos los campos
        clienteExistente.setIdentificacion(clienteDetalles.getIdentificacion()); // <-- AGREGAR ESTA LÍNEA
        clienteExistente.setNombre(clienteDetalles.getNombre());
        clienteExistente.setCorreoElectronico(clienteDetalles.getCorreoElectronico());
        clienteExistente.setDireccion(clienteDetalles.getDireccion());

        System.out.println("Guardando cliente: " + clienteExistente.getIdentificacion());
        return clienteRepository.save(clienteExistente);
    }
}