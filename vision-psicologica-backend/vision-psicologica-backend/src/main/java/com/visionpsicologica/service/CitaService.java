package com.visionpsicologica.service;

import com.visionpsicologica.model.CitaModel;
import com.visionpsicologica.model.ClienteModel;
import com.visionpsicologica.model.UsuarioModel;
import com.visionpsicologica.repository.CitaRepository;
import com.visionpsicologica.repository.ClienteRepository;
import com.visionpsicologica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final GoogleCalendarService googleCalendarService;
    private final ClienteRepository clienteRepository; // 3. Declaramos los repositorios faltantes
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CitaService(CitaRepository citaRepository,
                       GoogleCalendarService googleCalendarService,
                       ClienteRepository clienteRepository, // 4. Los sumamos al constructor
                       UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.googleCalendarService = googleCalendarService;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public CitaModel crearCita(CitaModel cita) {
        // 1. Validaciones de existencia básica de datos vinculados
        if (cita.getCliente() == null || cita.getCliente().getId() == null) {
            throw new IllegalArgumentException("La cita debe estar vinculada a un cliente válido");
        }
        if (cita.getPsicologo() == null || cita.getPsicologo().getId() == null) {
            throw new IllegalArgumentException("La cita debe estar asignada a un psicólogo válido");
        }

        // 🚀 LA SOLUCIÓN: Vamos a traer los registros completos desde la base de datos usando sus IDs
        ClienteModel clienteCompleto = clienteRepository.findById(cita.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente especificado no existe"));
        UsuarioModel psicologoCompleto = usuarioRepository.findById(cita.getPsicologo().getId())
                .orElseThrow(() -> new IllegalArgumentException("El psicólogo especificado no existe"));

        // Se los reasignamos al objeto cita para que vaya con la data cargada (incluyendo los emails)
        cita.setCliente(clienteCompleto);
        cita.setPsicologo(psicologoCompleto);

        // 2. Validación de coherencia temporal
        if (cita.getFechaInicio() == null || cita.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        if (cita.getFechaInicio().isAfter(cita.getFechaFin()) || cita.getFechaInicio().isEqual(cita.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser estrictamente anterior a la fecha de fin");
        }

        // 3. Validación de agenda: Evitar el cruce de horarios para el mismo psicólogo
        boolean horarioOcupado = citaRepository.existeCitaEnEseHorario(
                cita.getPsicologo().getId(),
                cita.getFechaInicio(),
                cita.getFechaFin()
        );

        if (horarioOcupado) {
            throw new IllegalArgumentException("El psicólogo ya tiene una cita agendada en ese rango de horario");
        }

        // Guardamos en la base de datos
        CitaModel citaGuardada = citaRepository.save(cita);

        // 🔍 LÍNEAS DE DEPURACIÓN EN VIVO: Vamos a desenmascarar qué está llegando aquí
        System.out.println("============= DEBUG GOOGLE CALENDAR =============");
        System.out.println("1. ¿Psicólogo completo es nulo? -> " + (citaGuardada.getPsicologo() == null));
        if (citaGuardada.getPsicologo() != null) {
            System.out.println("2. Email del Psicólogo obtenido -> [" + citaGuardada.getPsicologo().getEmail() + "]");
        }

        System.out.println("3. ¿Cliente completo es nulo? -> " + (citaGuardada.getCliente() == null));
        if (citaGuardada.getCliente() != null) {
            System.out.println("4. Email del Cliente obtenido -> [" + citaGuardada.getCliente().getCorreoElectronico() + "]");
        }
        System.out.println("=================================================");

        // El condicional que decide si dispara o no a Google
        if (citaGuardada.getPsicologo().getEmail() != null && citaGuardada.getCliente().getCorreoElectronico() != null) {
            googleCalendarService.agendarEventoCita(
                    citaGuardada.getPsicologo().getEmail(),
                    citaGuardada.getCliente().getCorreoElectronico(),
                    citaGuardada.getFechaInicio(),
                    citaGuardada.getFechaFin(),
                    citaGuardada.getCliente().getNombre()
            );
        }

        return citaGuardada;
    }

    public List<CitaModel> obtenerTodasLasCitas() {
        return citaRepository.findAll();
    }

    public Optional<CitaModel> obtenerCitaPorId(Long idCita) {
        return citaRepository.findById(idCita);
    }

    public List<CitaModel> obtenerCitasPorPsicologo(Long idPsicologo) {
        if (idPsicologo == null) {
            throw new IllegalArgumentException("El ID del psicólogo no puede ser nulo");
        }
        return citaRepository.findByPsicologoId(idPsicologo);
    }

    public List<CitaModel> obtenerCitasPorCliente(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo");
        }
        return citaRepository.findByClienteId(idCliente);
    }

    public List<CitaModel> obtenerCitasPorRangoDeFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new IllegalArgumentException("Rango de fechas inválido para la consulta");
        }
        return citaRepository.findByFechaInicioBetween(inicio, fin);
    }

    public void eliminarCita(Long idCita) {
        if (!citaRepository.existsById(idCita)) {
            throw new IllegalArgumentException("No se encontró la cita que deseas eliminar");
        }
        citaRepository.deleteById(idCita);
    }
}