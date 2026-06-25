package com.visionpsicologica.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.visionpsicologica.model.ClienteModel;
import com.visionpsicologica.model.ConsultaModel;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarPdfHistoriaClinica(ClienteModel cliente, ConsultaModel consulta, String edad) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            document.open();

            // Fondo / marca de agua opcional
            agregarImagenFondo(writer);

            // Encabezado
            agregarEncabezado(document);

            // Título de la sección
            agregarTitulo(document, "DATOS DE IDENTIFICACIÓN DEL USUARIO", 13);

            // Datos del usuario
            agregarBloqueIdentificacion(document, cliente, edad, consulta);

            // Secciones de la consulta
            agregarSeccion(document, "MOTIVO DE CONSULTA:", consulta.getMotivoConsulta());
            agregarSeccion(document, "EXAMEN MENTAL:", consulta.getExamenMental());
            agregarSeccion(document, "ANALISIS DEL CASO:", consulta.getAnalisisCaso());
            agregarSeccion(document, "PLAN DE MANEJO:", consulta.getPlanManejo());
            agregarSeccion(document, "DIAGNOSTICO:", consulta.getDiagnostico());

            // Firma / pie
            agregarPie(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando el PDF", e);
        }
    }

    private void agregarEncabezado(Document document) throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 4f});
        table.setSpacingAfter(15f);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        Image logo = cargarImagenClasspath("/pdf/logo.png");
        if (logo != null) {
            logo.scaleToFit(70, 70);
            logoCell.addElement(logo);
        }
        table.addCell(logoCell);

        PdfPCell textoCell = new PdfPCell();
        textoCell.setBorder(Rectangle.NO_BORDER);
        textoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font fontNombre = new Font(Font.HELVETICA, 12, Font.ITALIC, Color.DARK_GRAY);
        Font fontCargo = new Font(Font.HELVETICA, 11, Font.ITALIC, Color.DARK_GRAY);

        Paragraph p1 = new Paragraph("TAZANIA MOGOLLÓN", fontNombre);
        Paragraph p2 = new Paragraph("PSICOLOGA", fontCargo);

        p1.setSpacingAfter(3f);
        textoCell.addElement(p1);
        textoCell.addElement(p2);

        table.addCell(textoCell);

        document.add(table);
    }

    private void agregarTitulo(Document document, String titulo, int size) throws DocumentException {
        Font font = new Font(Font.HELVETICA, size, Font.BOLD, Color.BLACK);
        Paragraph p = new Paragraph(titulo, font);
        p.setSpacingBefore(5f);
        p.setSpacingAfter(12f);
        document.add(p);
    }

    private void agregarBloqueIdentificacion(Document document, ClienteModel cliente, String edad, ConsultaModel consulta) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f});
        table.setSpacingAfter(12f);

        Font labelFont = new Font(Font.HELVETICA, 10.5f, Font.BOLD, Color.BLACK);
        Font valueFont = new Font(Font.HELVETICA, 10.5f, Font.NORMAL, Color.BLACK);

        // Filas estándar
        table.addCell(celdaTexto("Identificación: " + safe(cliente.getIdentificacion()), labelFont, valueFont));
        table.addCell(celdaTexto("Nombre: " + safe(cliente.getNombre()), labelFont, valueFont));

        table.addCell(celdaTexto("Edad: " + safe(edad), labelFont, valueFont));
        table.addCell(celdaTexto("Dirección: " + safe(cliente.getDireccion()), labelFont, valueFont));

        table.addCell(celdaTexto("Acompañante: " + safe(cliente.getAcompañante()), labelFont, valueFont));
        table.addCell(celdaTexto("Teléfono: " + safe(cliente.getTelefono()), labelFont, valueFont));

        // --- CORRECCIÓN: Fila de Fecha de Atención ocupando ambas columnas ---
        String fechaStr = "-";
        if (consulta.getFechaConsulta() != null) {
            // Si tu consulta usa LocalDateTime o similar, aplicamos el formateador de tu compañero
            fechaStr = consulta.getFechaConsulta().format(FECHA_FORMATO);
        }

        PdfPCell celdaFecha = celdaTexto("Fecha de atención: " + fechaStr, labelFont, valueFont);
        celdaFecha.setColspan(2); // Hace que se expanda a lo ancho de la tabla
        celdaFecha.setPaddingTop(4f);
        table.addCell(celdaFecha);

        document.add(table);
    }

    private PdfPCell celdaTexto(String texto, Font labelFont, Font valueFont) {
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(texto, valueFont));

        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(8f);
        return cell;
    }

    private void agregarSeccion(Document document, String titulo, String contenido) throws DocumentException {
        Font tituloFont = new Font(Font.HELVETICA, 13, Font.BOLD, Color.BLACK);
        Font contenidoFont = new Font(Font.HELVETICA, 10.5f, Font.NORMAL, Color.BLACK);

        Paragraph pTitulo = new Paragraph(titulo, tituloFont);
        pTitulo.setSpacingBefore(12f);
        pTitulo.setSpacingAfter(8f);
        document.add(pTitulo);

        Paragraph pContenido = new Paragraph(safe(contenido), contenidoFont);
        pContenido.setAlignment(Element.ALIGN_JUSTIFIED);
        pContenido.setLeading(14f);
        pContenido.setSpacingAfter(8f);
        document.add(pContenido);
    }

    private void agregarPie(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        Font fontPie = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

        Paragraph p1 = new Paragraph("TAZANIA MOGOLLÓN", fontPie);
        Paragraph p2 = new Paragraph("PSICOLOGA", fontPie);
        Paragraph p3 = new Paragraph("TP.276538", fontPie);

        p1.setSpacingBefore(20f);
        document.add(p1);
        document.add(p2);
        document.add(p3);
    }

    private void agregarImagenFondo(PdfWriter writer) {
        try {
            Image fondo = cargarImagenClasspath("/pdf/fondo.png");
            if (fondo == null) {
                return;
            }

            fondo.scaleToFit(450, 450);
            fondo.setAbsolutePosition(80, 220);

            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.saveState();

            // Opacidad suave si la imagen lo soporta
            PdfGState gState = new PdfGState();
            gState.setFillOpacity(0.15f);
            gState.setStrokeOpacity(0.15f);
            canvas.setGState(gState);

            canvas.addImage(fondo);

            canvas.restoreState();
        } catch (Exception ignored) {
            // Si no existe la imagen, el PDF igual se genera
        }
    }

    private Image cargarImagenClasspath(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            return Image.getInstance(is.readAllBytes());
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}