package com.visionpsicologica.model;

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
@Table(name = "consultas_sesiones")
public class ConsultaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long id;

    @Column(name = "id_historia", nullable = false)
    private Long idHistoria;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Builder.Default
    @Column(name = "fecha_consulta", nullable = false, updatable = false)
    private LocalDateTime fechaConsulta = LocalDateTime.now();

    @Column(name = "motivo_consulta", nullable = false, columnDefinition = "TEXT")
    private String motivoConsulta;

    @Column(name = "examen_mental", columnDefinition = "TEXT")
    private String examenMental;

    @Column(name = "analisis_caso", columnDefinition = "TEXT")
    private String analisisCaso;

    @Column(name = "plan_manejo", columnDefinition = "TEXT")
    private String planManejo;

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "id_psicologo", nullable = false)
    private Long idPsicologo;
}