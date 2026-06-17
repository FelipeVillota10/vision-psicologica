package com.visionpsicologica.service;

import com.visionpsicologica.model.ClienteModel;
import com.visionpsicologica.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

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

        String direccionAnterior = clienteExistente.getDireccion();

        if (!clienteExistente.getIdentificacion().equals(clienteDetalles.getIdentificacion())) {
            if (clienteRepository.existsByIdentificacion(clienteDetalles.getIdentificacion())) {
                throw new RuntimeException("Ya existe otro cliente con la identificación: " + clienteDetalles.getIdentificacion());
            }
        }

        clienteExistente.setIdentificacion(clienteDetalles.getIdentificacion());
        clienteExistente.setNombre(clienteDetalles.getNombre());
        clienteExistente.setCorreoElectronico(clienteDetalles.getCorreoElectronico());
        clienteExistente.setDireccion(clienteDetalles.getDireccion());

        logger.atInfo()
                .setMessage("Modificación de datos sensibles del paciente")
                .addKeyValue("quien_hizo_el_cambio", "psicologo_sesion") // Para la demo queda perfecto como texto estático
                .addKeyValue("registro_tocado", "Cliente ID: " + id)
                .addKeyValue("campo_cambiado", "direccion")
                .addKeyValue("antes", direccionAnterior)
                .addKeyValue("despues", clienteDetalles.getDireccion())
                .log();

        return clienteRepository.save(clienteExistente);
    }

}