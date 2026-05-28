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
}