package com.visionpsicologica.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios_historias")
public class UsuarioHistoriaModel {

    @EmbeddedId
    @Builder.Default // 1. ¡CLAVE! Obliga a Lombok Builder a respetar la inicialización por defecto
    private UsuarioHistoriaId id = new UsuarioHistoriaId();

    // --- RELACIÓN CON EL PSICÓLOGO (USUARIO) ---
    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"clientes", "contrasena"})
    private UsuarioModel usuario;

    // --- RELACIÓN CON EL EXPEDIENTE (HISTORIA CLÍNICA) ---
    @ManyToOne
    @MapsId("idHistoria")
    @JoinColumn(name = "id_historia")
    @JsonIgnoreProperties("cliente")
    private HistoriaClinicaModel historiaClinica;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    /**
     * Setea de forma automática la fecha y hora exacta.
     * Ya no creamos el 'new UsuarioHistoriaId' aquí porque @MapsId
     * se encarga de rellenar los campos internamente de forma nativa.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaAsignacion = LocalDateTime.now();
    }
}