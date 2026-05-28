package com.visionpsicologica.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "cliente")
public class ClienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HU-5 Punto 3 y 4: Obligatorio y solo caracteres alfabéticos
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener caracteres alfabéticos")
    @Column(nullable = false)
    private String nombre;

    // HU-5 Punto 3 y 5: Obligatorio y solo numéricos
    @NotBlank(message = "La identificación es obligatoria")
    @Pattern(regexp = "^[0-9]+$", message = "La identificación solo puede contener caracteres numéricos")
    @Column(nullable = false, unique = true)
    private String identificacion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ser un correo válido")
    @Column(name = "correo_electronico", nullable = false)
    private String correoElectronico;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String direccion;

    // Relación con el psicólogo (Usuario) - Asumiendo que tu modelo de usuario se llama UsuarioModel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    // Constructores, Getters y Setters
    public ClienteModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }
}