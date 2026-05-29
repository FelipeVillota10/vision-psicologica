package com.visionpsicologica.controller;

import com.visionpsicologica.model.HistoriaClinicaModel;
import com.visionpsicologica.service.HistoriaClinicaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/historias-clinicas")
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService historiaClinicaService;


    @PostMapping
    public ResponseEntity<?> crearHistoriaClinica(@Valid @RequestBody HistoriaClinicaModel historiaClinica) {
        try {
            HistoriaClinicaModel nuevaHistoria = historiaClinicaService.guardarHistoriaClinica(historiaClinica);
            return new ResponseEntity<>(nuevaHistoria, HttpStatus.CREATED);
        } catch (RuntimeException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> obtenerPorClienteId(@PathVariable Long idCliente) {
        Optional<HistoriaClinicaModel> historia = historiaClinicaService.obtenerPorClienteId(idCliente);

        if (historia.isPresent()) {
            return new ResponseEntity<>(historia.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>("No se encontró ninguna historia clínica asociada al ID de cliente: " + idCliente, HttpStatus.NOT_FOUND);
    }
}