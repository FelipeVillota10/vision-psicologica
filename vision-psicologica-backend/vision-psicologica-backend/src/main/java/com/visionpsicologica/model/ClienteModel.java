package com.visionpsicologica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="clientes")
public class ClienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "identificacion", nullable = false, length = 50, unique = true)
    private String identificacion;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "email", nullable = false, length = 100) // Cambiamos el nombre aquí
    private String correoElectronico;

    @Column(name = "direccion", nullable = false, length = 150)
    private String direccion;

    @Column(name = "acompañante", columnDefinition = "TEXT")
    private String acompañante;

    // --- AQUÍ ESTÁ LA MAGIA DE LA TABLA INTERMEDIA ---
    @ManyToMany
    @JoinTable(
            name = "usuarios_clientes",
            joinColumns = @JoinColumn(name = "id_cliente"), // <-- Cambiamos a id_cliente
            inverseJoinColumns = @JoinColumn(name = "id_usuario") // <-- Cambiamos a id_usuario
    )
    private List<UsuarioModel> usuarios;
}