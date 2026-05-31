package com.visionpsicologica.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Vision Psicologica";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Calendar obtenerClienteCalendar() throws Exception {
        // Lee el archivo JSON desde la carpeta resources
        try (InputStream in = getClass().getResourceAsStream("/google-credentials.json")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el archivo google-credentials.json en resources");
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }

    public void agendarEventoCita(String emailPsicologo, String emailCliente, LocalDateTime inicio, LocalDateTime fin, String nombreCliente) {
        try {
            System.out.println("=== 📦 DENTRO DE GOOGLE CALENDAR SERVICE ===");
            System.out.println("-> Recibido emailPsicologo: [" + emailPsicologo + "]");

            Calendar service = obtenerClienteCalendar();

            // 1. Configuramos los datos básicos del evento
            Event event = new Event()
                    .setSummary("Sesión de Psicología - " + nombreCliente)
                    .setDescription("Cita médica agendada para el paciente " + nombreCliente + " a través de la plataforma Visión Psicológica.");

            // 2. Ajustamos las fechas y horas con la zona horaria de Colombia
            ZoneId zoneId = ZoneId.of("America/Bogota");
            ZonedDateTime zdtInicio = inicio.atZone(zoneId);
            ZonedDateTime zdtFin = fin.atZone(zoneId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(zdtInicio.format(formatter)))
                    .setTimeZone("America/Bogota");
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(zdtFin.format(formatter)))
                    .setTimeZone("America/Bogota");
            event.setEnd(end);

            // 🌟 NOTA: Aquí se eliminó el bloque de 'attendees' (invitados) para saltarnos el error 403.

            // 3. Apuntamos el destino directamente al correo del psicólogo
            String calendarId = emailPsicologo;

            System.out.println("-> Intentando insertar en Google Calendar ID: [" + calendarId + "]");
            System.out.println("=============================================");

            // 4. Enviamos la petición a la API de Google
            service.events().insert(calendarId, event)
                    .setSendUpdates("all")
                    .execute();

            System.out.println("✅ ¡Evento creado con éxito en la agenda del psicólogo!");

        } catch (Exception e) {
            System.err.println("❌ Error al crear el evento en Google Calendar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}