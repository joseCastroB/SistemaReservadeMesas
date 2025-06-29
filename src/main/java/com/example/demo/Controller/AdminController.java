package com.example.demo.Controller;

import com.example.demo.Entities.Reserva;
import com.example.demo.Repository.ConfiguracionFranjaRepository;
import com.example.demo.Repository.TipoMesaRepository;
import com.example.demo.Services.ReservaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin") // Todas las rutas en este controlador empezarán con /admin
public class AdminController {

    private final ReservaService reservaService;
    private final TipoMesaRepository tipoMesaRepository;
    private final ConfiguracionFranjaRepository franjaRepository;

    public AdminController(ReservaService reservaService, TipoMesaRepository tipoMesaRepository, ConfiguracionFranjaRepository franjaRepository) {
        this.reservaService = reservaService;
        this.tipoMesaRepository = tipoMesaRepository;
        this.franjaRepository = franjaRepository;
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
}