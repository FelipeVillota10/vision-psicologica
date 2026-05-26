package com.visionpsicologica.service;

import com.visionpsicologica.model.UsuarioModel;
import com.visionpsicologica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioModel createUsuario(UsuarioModel usuarioModel) {

        if (usuarioModel.getContrasena() == null || usuarioModel.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        if (usuarioRepository.findByIdentificacion(usuarioModel.getIdentificacion()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con esa identificación");
        }

        if (usuarioRepository.findByEmail(usuarioModel.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        return usuarioRepository.save(usuarioModel);
    }

    // Actualizar usuario (también sin hash)
    public UsuarioModel updateUsuario(UsuarioModel usuarioModel) {

        if (usuarioModel.getContrasena() != null && !usuarioModel.getContrasena().isEmpty()) {
        }
        return usuarioRepository.save(usuarioModel);
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioModel> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioModel> findByIdentificacion(String identificacion) {
        return usuarioRepository.findByIdentificacion(identificacion);
    }

    public Optional<UsuarioModel> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<UsuarioModel> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<UsuarioModel> findAllByNombre(String nombre) {
        return usuarioRepository.findAllByNombre(nombre);
    }

    public boolean autenticar(String email, String contrasenaPlana) {
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        UsuarioModel usuario = usuarioOpt.get();
        return usuario.getContrasena().equals(contrasenaPlana);
    }
}