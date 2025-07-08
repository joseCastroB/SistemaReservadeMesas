package com.example.demo.Controller;

import com.example.demo.Entities.Reserva;
import com.example.demo.dto.ReporteDTO;
import com.example.demo.dto.ReservaFormDTO;
import com.example.demo.Repository.ConfiguracionFranjaRepository;
import com.example.demo.Repository.TipoMesaRepository;
import com.example.demo.Services.ReservaService;
import com.example.demo.Services.ReporteService;
import com.example.demo.Services.ReportExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin") // Todas las rutas en este controlador empezarán con /admin
public class AdminController {

    private final ReservaService reservaService;
    private final ReporteService reporteService;
    private final ReportExportService exportService;
    private final TipoMesaRepository tipoMesaRepository;
    private final ConfiguracionFranjaRepository franjaRepository;

    public AdminController(ReservaService reservaService,
                            ReporteService reporteService, 
                            TipoMesaRepository tipoMesaRepository, 
                            ConfiguracionFranjaRepository franjaRepository, 
                            ReportExportService exportService) {
        this.reservaService = reservaService;
        this.reporteService = reporteService;
        this.tipoMesaRepository = tipoMesaRepository;
        this.franjaRepository = franjaRepository;
        this.exportService = exportService;
    }

    /**
     * Muestra la página principal del administrador, cargando las reservas del día actual.
     */
    @GetMapping
    public String mostrarPanelAdmin(Model model) {
        LocalDate hoy = LocalDate.now();
        List<Reserva> reservasDelDia = reservaService.findWithFilters(hoy, null, null, null, null);
        
        model.addAttribute("reservas", reservasDelDia);
        model.addAttribute("fechaSeleccionada", hoy);
        
        // Carga los datos para poblar los dropdowns de los filtros
        model.addAttribute("tiposDeMesa", tipoMesaRepository.findAll());
        model.addAttribute("franjasHorarias", franjaRepository.findAll());
        
        return "admin";
    }
    
    /**
     * API MODIFICADA: Ahora acepta todos los parámetros de filtro.
     */
    @GetMapping("/api/reservas")
    @ResponseBody
    public List<Reserva> getReservasFiltradas(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "idTipoMesa", required = false) Integer idTipoMesa,
            @RequestParam(value = "idFranja", required = false) Integer idFranja,
            @RequestParam(value = "estado", required = false) String estado) {
        return reservaService.findWithFilters(fecha, nombre, idTipoMesa, idFranja, estado);
    }
    
    /**
     * Endpoint para actualizar el estado de una reserva.
     */
    @PostMapping("/api/reservas/actualizar-estado")
    @ResponseBody
    public String actualizarEstadoReserva(@RequestParam Integer idReserva, @RequestParam String estado) {
        try {
            reservaService.actualizarEstadoReserva(idReserva, estado);
            return "{\"status\":\"success\", \"message\":\"Estado actualizado correctamente.\"}";
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}";
        }
    }

    // --- NUEVO MÉTODO GET: Para mostrar la página de reservas anónimas ---
    @GetMapping("/reservas-anonimas")
    public String mostrarFormularioReservasAnonimas(Model model) {
        // Pasamos los datos necesarios para los dropdowns y el formulario
        model.addAttribute("reservaForm", new ReservaFormDTO());
        model.addAttribute("tiposDeMesa", tipoMesaRepository.findAll());
        model.addAttribute("fechaMin", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        model.addAttribute("fechaMax", "2025-12-31");
        return "reservasanonimas"; // Devuelve reservasanonimas.html
    }

    // --- NUEVO MÉTODO POST: Para crear una reserva anónima ---
    @PostMapping("/reservas-anonimas/crear")
    public String procesarReservaAnonima(@ModelAttribute("reservaForm") ReservaFormDTO formDTO,
                                          Authentication authentication,
                                          RedirectAttributes redirectAttributes) {
        try {
            // Pasamos el nombre de usuario del admin para registrar quién hizo la reserva
            String adminUsername = authentication.getName();
            reservaService.crearReservaAnonima(formDTO, adminUsername);
            redirectAttributes.addFlashAttribute("successMessage", "¡Reserva anónima registrada exitosamente!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar la reserva: " + e.getMessage());
        }
        
        return "redirect:/admin/reservas-anonimas";
    }

     // --- SECCIÓN DE REPORTES ---

    @GetMapping("/reportes")
    public String mostrarPaginaReportes(Model model) {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.withDayOfMonth(1);
        ReporteDTO reporte = reporteService.generarReporte(inicio, fin, null, null);
        
        model.addAttribute("reporte", reporte);
        model.addAttribute("fechaInicio", inicio);
        model.addAttribute("fechaFin", fin);
        model.addAttribute("tiposDeMesa", tipoMesaRepository.findAll());
        
        return "reportes";
    }

    @GetMapping("/api/reportes")
    @ResponseBody
    public ReporteDTO getReporteFiltrado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idTipoMesa) {
        return reporteService.generarReporte(fechaInicio, fechaFin, estado, idTipoMesa);
    }

    // --- NUEVO ENDPOINT PARA EXPORTAR A EXCEL ---
    @GetMapping("/reportes/exportar-excel")
    public ResponseEntity<InputStreamResource> exportarExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idTipoMesa) throws IOException {
        
        List<Reserva> reservas = reporteService.generarReporte(fechaInicio, fechaFin, estado, idTipoMesa).getReservas();
        ByteArrayInputStream in = exportService.exportarExcel(reservas);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_reservas.xlsx");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    // --- NUEVO ENDPOINT PARA EXPORTAR A PDF ---
    @GetMapping("/reportes/exportar-pdf")
    public ResponseEntity<InputStreamResource> exportarPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idTipoMesa) {
        
        List<Reserva> reservas = reporteService.generarReporte(fechaInicio, fechaFin, estado, idTipoMesa).getReservas();
        ByteArrayInputStream in = exportService.exportarPdf(reservas);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_reservas.pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }

}