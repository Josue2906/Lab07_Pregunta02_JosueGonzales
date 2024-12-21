package com.example.Lab07_Pregunta02_JosueGonzales.controller;

import com.example.Lab07_Pregunta02_JosueGonzales.model.Mascota;
import com.example.Lab07_Pregunta02_JosueGonzales.service.MascotaService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {
    private final MascotaService service;

    public MascotaController(MascotaService service) {
        this.service = service;
    }

    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", service.listarTodas());
        return "lista_mascotas"; // Vista de listado
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("mascota", new Mascota());
        return "formulario_mascotas";
    }

    @PostMapping
    public String guardarMascota(@ModelAttribute Mascota mascota) {
        service.guardar(mascota);
        return "redirect:/mascotas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Mascota mascota = service.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("mascota", mascota);
        return "formulario_mascotas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/mascotas";
    }

    @GetMapping("/reporte/pdf")
    public void generarReportePDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=mascotas_reporte.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        Document document = new Document(new PdfDocument(writer));

        document.add(new Paragraph("Reporte de Mascotas").setBold().setFontSize(18));

        Table table = new Table(5); // ID, Nombre, Edad, Especie, Raza
        table.addCell("ID");
        table.addCell("Nombre");
        table.addCell("Edad");
        table.addCell("Especie");
        table.addCell("Raza");

        service.listarTodas().forEach(mascota -> {
            table.addCell(mascota.getId().toString());
            table.addCell(mascota.getNombre());
            table.addCell(String.valueOf(mascota.getEdad()));
            table.addCell(mascota.getEspecie());
            table.addCell(mascota.getRaza());
        });

        document.add(table);
        document.close();
    }

    @GetMapping("/reporte/excel")
    public ResponseEntity<byte[]> generarReporteExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Mascotas");

        Row header = sheet.createRow(0);
        String[] headersArray = {"ID", "Nombre", "Edad", "Especie", "Raza"};
        for (int i = 0; i < headersArray.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headersArray[i]);
        }

        int rowNum = 1;
        for (Mascota mascota : service.listarTodas()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(mascota.getId());
            row.createCell(1).setCellValue(mascota.getNombre());
            row.createCell(2).setCellValue(mascota.getEdad());
            row.createCell(3).setCellValue(mascota.getEspecie());
            row.createCell(4).setCellValue(mascota.getRaza());
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=mascotas_reporte.xlsx");

        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }
}
