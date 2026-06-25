package com.visionpsicologica.controller;

import com.visionpsicologica.model.ConsultaModel;
import com.visionpsicologica.repository.HistoriaClinicaRepository;
import com.visionpsicologica.service.ConsultaService;
import com.visionpsicologica.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.visionpsicologica.model.ClienteModel;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consulta")
@CrossOrigin(origins = "http://localhost:5173")
public class ConsultaController {
    private final PdfService pdfService;
    private final ConsultaService consultaService;
    private final HistoriaClinicaRepository historiaClinicaRepository;

    @Autowired
    public ConsultaController(ConsultaService consultaService, PdfService pdfService, HistoriaClinicaRepository historiaClinicaRepository) {
        this.consultaService = consultaService;
        this.pdfService = pdfService;
        this.historiaClinicaRepository = historiaClinicaRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<ConsultaModel> guardarConsulta(@RequestBody ConsultaModel consultaModel) {
        ConsultaModel consultaGuardada = consultaService.guardar(consultaModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaGuardada);
    }

    @GetMapping
    public List<ConsultaModel> obtenerTodas() {
        return consultaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaModel> obtenerPorId(@PathVariable Long id) {
        Optional<ConsultaModel> consultaOpt = consultaService.obtenerPorId(id);
        return consultaOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/historia/{idHistoria}")
    public List<ConsultaModel> obtenerPorHistoria(@PathVariable Long idHistoria) {
        return consultaService.obtenerPorHistoria(idHistoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConsulta(@PathVariable Long id) {
        Optional<ConsultaModel> consultaOpt = consultaService.obtenerPorId(id);

        if (consultaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        consultaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historia/{idHistoria}/pdf")
    public ResponseEntity<byte[]> descargarHistoriaPdf(@PathVariable Long idHistoria) {

        // 1. Buscamos las consultas asociadas a la historia clínica
        List<ConsultaModel> consultas = consultaService.obtenerPorHistoria(idHistoria);
        if (consultas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Tomamos la consulta para el reporte
        ConsultaModel consulta = consultas.get(0);

        // 2. Buscamos la historia clínica en la BD para poder llegar al cliente
        var historiaOpt = historiaClinicaRepository.findById(idHistoria);
        if (historiaOpt.isEmpty() || historiaOpt.get().getCliente() == null) {
            return ResponseEntity.badRequest().build();
        }

        var historia = historiaOpt.get();
        ClienteModel cliente = historia.getCliente();

        // 3. SOLUCIÓN A LA EDAD:
        // Como tu ClienteModel no tiene edad, por ahora enviamos un guion "-" para cumplir con la plantilla.
        // NOTA: Si en el futuro guardas la edad en 'HistoriaClinicaModel', aquí podrías hacer:
        // String edad = historia.getEdad() != null ? historia.getEdad().toString() : "-";
        String edad = "-";

        // 4. Invocamos el servicio de tu compañero enviando las entidades reales y la edad externa
        byte[] pdfBytes = pdfService.generarPdfHistoriaClinica(cliente, consulta, edad);

        // 5. Configuramos las cabeceras HTTP para que el navegador lo abra como PDF nativo
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);

        // "inline" permite que se abra en una pestaña nueva de Vue de forma limpia
        headers.setContentDispositionFormData("inline", "Historia_Clinica_" + idHistoria + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
