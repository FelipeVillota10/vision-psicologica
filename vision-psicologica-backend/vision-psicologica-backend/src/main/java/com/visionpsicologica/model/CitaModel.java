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
@Table(name = "citas")
public class CitaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long id;

    // Relación con el Cliente: Muchas citas pueden pertenecer a un mismo cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties("usuarios") // Evita traer relaciones inversas innecesarias en el JSON de la cita
    private ClienteModel cliente;

    // Relación con el Psicólogo (Usuario): Muchas citas pueden ser atendidas por un mismo psicólogo
    @ManyToOne
    @JoinColumn(name = "id_psicologo", nullable = false)
    @JsonIgnoreProperties({"clientes", "contrasena"}) // Oculta datos sensibles y bucles al serializar la cita
    private UsuarioModel psicologo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;
}