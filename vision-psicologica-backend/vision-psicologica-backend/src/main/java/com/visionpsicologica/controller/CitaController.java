package com.visionpsicologica.controller;

import com.visionpsicologica.model.CitaModel;
import com.visionpsicologica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = "http://localhost:5173")
public class CitaController {

    private final CitaService citaService;

    @Autowired
    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }


    @PostMapping
    public ResponseEntity<?> crearCita(@RequestBody CitaModel citaModel) {
        try {
            CitaModel nuevaCita = citaService.crearCita(citaModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita);
        } catch (IllegalArgumentException e) {
            // Retorna un 400 Bad Request con la razón exacta (ej: "El psicólogo ya tiene una cita...")
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<CitaModel>> obtenerTodasLasCitas() {
        return ResponseEntity.ok(citaService.obtenerTodasLasCitas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaModel> obtenerCitaPorId(@PathVariable Long id) {
        return citaService.obtenerCitaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<?> obtenerCitasPorPsicologo(@PathVariable Long idPsicologo) {
        try {
            List<CitaModel> citas = citaService.obtenerCitasPorPsicologo(idPsicologo);
            return ResponseEntity.ok(citas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> obtenerCitasPorCliente(@PathVariable Long idCliente) {
        try {
            List<CitaModel> citas = citaService.obtenerCitasPorCliente(idCliente);
            return ResponseEntity.ok(citas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/rango")
    public ResponseEntity<?> obtenerCitasPorRango(
            @RequestParam(name = "inicio") String inicioStr,
            @RequestParam(name = "fin") String finStr) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(inicioStr);
            LocalDateTime fin = LocalDateTime.parse(finStr);
            List<CitaModel> citas = citaService.obtenerCitasPorRangoDeFechas(inicio, fin);
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Formato de parámetros inválido. Use el estándar ISO-8601 (YYYY-MM-DDTHH:mm:ss)"
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCita(@PathVariable Long id) {
        try {
            citaService.eliminarCita(id);
            return ResponseEntity.ok().body(Map.of("message", "La cita ha sido cancelada y eliminada exitosamente del sistema"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}