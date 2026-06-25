package com.visionpsicologica.controller;

import com.visionpsicologica.model.UsuarioHistoriaModel;
import com.visionpsicologica.service.UsuarioHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios-historias")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioHistoriaController {

    private final UsuarioHistoriaService usuarioHistoriaService;

    @Autowired
    public UsuarioHistoriaController(UsuarioHistoriaService usuarioHistoriaService) {
        this.usuarioHistoriaService = usuarioHistoriaService;
    }

    @GetMapping("/historia/{idHistoria}")
    public ResponseEntity<List<UsuarioHistoriaModel>> listarUsuariosPorHistoriaId(@PathVariable Long idHistoria) {
        try {
            List<UsuarioHistoriaModel> accesos = usuarioHistoriaService.listarUsuariosPorHistoriaId(idHistoria);
            return ResponseEntity.ok(accesos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/asignar")
    public ResponseEntity<?> asignarAcceso(@RequestBody UsuarioHistoriaModel usuarioHistoriaModel) {
        try {
            UsuarioHistoriaModel nuevaAsignacion = usuarioHistoriaService.asignarAcceso(usuarioHistoriaModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignacion);
        } catch (IllegalArgumentException e) {
            // Retorna el mensaje controlado en el Service (ej: "El usuario ya se encuentra autorizado")
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/revocar")
    public ResponseEntity<?> revocarAcceso(
            @RequestParam(name = "idUsuario") Long idUsuario,
            @RequestParam(name = "idHistoria") Long idHistoria) {
        try {
            usuarioHistoriaService.revocarAcceso(idUsuario, idHistoria);
            return ResponseEntity.ok().body(Map.of("message", "Acceso revocado exitosamente del sistema"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}