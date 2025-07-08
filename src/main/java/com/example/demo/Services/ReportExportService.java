package com.example.demo.Services;

import com.example.demo.Entities.Reserva;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class ReportExportService {
    
    /**
     * Crea un archivo Excel a partir de una lista de reservas.
     */
    public ByteArrayInputStream exportarExcel(List<Reserva> reservas) throws IOException {
        String[] columns = {"Nombre Cliente", "Fecha", "Hora", "Tipo Mesa", "Estado", "Personas"};
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("Reservas");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(reserva.getNombreCliente());
                row.createCell(1).setCellValue(reserva.getFecha().format(formatter));
                row.createCell(2).setCellValue(reserva.getFranja().getFranjaHoraria());
                row.createCell(3).setCellValue(reserva.getTipoMesa().getNombre());
                row.createCell(4).setCellValue(reserva.getEstado());
                row.createCell(5).setCellValue(reserva.getNumeroPersonas());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    /**
     * Crea un archivo PDF a partir de una lista de reservas.
     */
    public ByteArrayInputStream exportarPdf(List<Reserva> reservas) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(Color.GREEN);
            Paragraph title = new Paragraph("Reporte de Reservas de Siete Sopas", font);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            // Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Header
            String[] headers = {"Nombre Cliente", "Fecha", "Hora", "Tipo Mesa", "Estado", "Personas"};
            for(String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Reserva reserva : reservas) {
                table.addCell(reserva.getNombreCliente());
                table.addCell(reserva.getFecha().format(formatter));
                table.addCell(reserva.getFranja().getFranjaHoraria());
                table.addCell(reserva.getTipoMesa().getNombre());
                table.addCell(reserva.getEstado());
                table.addCell(String.valueOf(reserva.getNumeroPersonas()));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
