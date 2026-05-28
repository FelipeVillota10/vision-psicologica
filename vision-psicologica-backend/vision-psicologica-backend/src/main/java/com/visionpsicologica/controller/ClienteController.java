package com.visionpsicologica.controller;

import com.visionpsicologica.model.ClienteModel;
import com.visionpsicologica.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // HU-5 Punto 6: Botón "Guardar cliente" activa este endpoint
    @PostMapping
    public ResponseEntity<?> crearCliente(@Valid @RequestBody ClienteModel cliente) {
        try {
            ClienteModel nuevoCliente = clienteService.guardarCliente(cliente);
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}