package com.visionpsicologica.controller;

import com.visionpsicologica.model.ClienteModel;
import com.visionpsicologica.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// 1. Agregas esta importación
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
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

    @GetMapping
    public ResponseEntity<List<ClienteModel>> listarClientes() {
        try {
            List<ClienteModel> clientes = clienteService.obtenerTodosLosClientes();
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @ResponseBody // Añade esto para asegurar la serialización
    public ResponseEntity<ClienteModel> actualizarCliente(@PathVariable Long id, @RequestBody ClienteModel cliente) {
        // Imprime lo que llega al controlador
        System.out.println("Backend recibió: " + cliente.getIdentificacion());

        ClienteModel actualizado = clienteService.actualizarCliente(id, cliente);
        return ResponseEntity.ok(actualizado);
    }


}