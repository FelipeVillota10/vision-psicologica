package com.visionpsicologica.controller;

import com.visionpsicologica.model.UsuarioModel;
import com.visionpsicologica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private UsuarioService usuarioService;

    @Autowired
    public UsuarioController (UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/save")
    public UsuarioModel saveUsuario(@RequestBody UsuarioModel usuarioModel){
        return usuarioService.createUsuario(usuarioModel);
    }

    @GetMapping
    public List<UsuarioModel> findAllUsuarios(){
        return usuarioService.findAllUsuarios();
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario (@RequestParam (name="id") Long id){
        usuarioService.deleteUsuario(id);
    }

    @GetMapping("/buscar")
    public Optional<UsuarioModel> findByIdentificacion(@RequestParam(name="identificacion") String identificacion){
        return usuarioService.findByIdentificacion(identificacion);
    }

    @GetMapping("/buscar/nombre")
    public List<UsuarioModel> findAllByNombre(@RequestParam(name="nombre") String nombre){
        return usuarioService.findAllByNombre(nombre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> updateUsuario(@PathVariable Long id, @RequestBody UsuarioModel usuarioModel) {
        usuarioModel.setId(id);
        try {
            UsuarioModel actualizado = usuarioService.updateUsuario(usuarioModel);
            actualizado.setContrasena(null);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> findById(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(usuario -> {
                    usuario.setContrasena(null);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String contrasena = credentials.get("contrasena");
        boolean esValido = usuarioService.autenticar(email, contrasena);
        if (esValido) {
            return ResponseEntity.ok().body(Map.of("message", "Autenticación exitosa"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email o contraseña incorrectos"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUsuario() {
        return ResponseEntity.ok().body(Map.of("message", "Sesión cerrada exitosamente"));
    }

    // HU-4 Criterio 3: Endpoint para solicitar la clave temporal
    @PostMapping("/recuperar-solicitud")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String identificacion = payload.get("identificacion");

        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarParaRecuperacion(email, identificacion);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "El usuario no se encuentra registrado en el sistema."));
        }

        UsuarioModel usuario = usuarioOpt.get();
        String claveTemporalSimulada = "12345";

        return ResponseEntity.ok().body(Map.of(
                "message", "Se ha enviado una contraseña temporal al correo: " + usuario.getEmail(),
                "idUsuario", usuario.getId(),
                "emailUsuario", usuario.getEmail(),
                "claveTemporal", claveTemporalSimulada
        ));
    }

    // HU-4 Criterio 5: Endpoint para guardar la nueva contraseña definitiva
    @PostMapping("/guardar-nueva-contrasena")
    public ResponseEntity<?> guardarNuevaContrasena(@RequestBody Map<String, Object> payload) {
        Long idUsuario = Long.valueOf(payload.get("idUsuario").toString());
        String nuevaContrasena = payload.get("nuevaContrasena").toString();

        boolean exito = usuarioService.actualizarContrasena(idUsuario, nuevaContrasena);

        if (exito) {
            return ResponseEntity.ok().body(Map.of("message", "¡Cambio exitoso! La nueva contraseña se ha actualizado correctamente en la Base de Datos."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No se pudo actualizar la contraseña."));
        }
    }

}
