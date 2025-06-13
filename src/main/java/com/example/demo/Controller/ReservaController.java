package com.example.demo.Controller;

import com.example.demo.dto.DisponibilidadTipoMesaDTO;
import com.example.demo.dto.FranjaDisponibleDTO;
import com.example.demo.dto.DisponibilidadTipoMesaDTO;
import com.example.demo.dto.ReservaFormDTO;
import com.example.demo.Entities.TipoMesa;
import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.TipoMesaRepository;
import com.example.demo.Repository.UsuarioRepository;
import com.example.demo.Services.ReservaService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;
    private final TipoMesaRepository tipoMesaRepository;

    public ReservaController(ReservaService reservaService, UsuarioRepository usuarioRepository, TipoMesaRepository tipoMesaRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
        this.tipoMesaRepository = tipoMesaRepository;
    }

    @GetMapping("/reservaciones")
    public String mostrarFormularioReserva(Model model, Authentication authentication) {
        // Creamos el objeto DTO que usará el formulario
        ReservaFormDTO reservaForm = new ReservaFormDTO();

        // Regla 2: Autocompletar datos si el usuario está logueado
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            // Buscamos el usuario y si existe, poblamos el DTO
            usuarioRepository.findByUsuario(username).ifPresent(usuario -> {
                reservaForm.setNombreCliente(usuario.getNombreCompleto());
                reservaForm.setCorreoCliente(usuario.getCorreo());
                reservaForm.setTelefonoCliente(usuario.getTelefono());
            });
        }
        
        // Pasamos el DTO (ya poblado o vacío) al modelo
        model.addAttribute("reservaForm", reservaForm);
        
        // Pasamos los otros datos necesarios para la vista
        model.addAttribute("tiposDeMesa", tipoMesaRepository.findAll());
        model.addAttribute("fechaMin", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        model.addAttribute("fechaMax", "2025-12-31");
        
        return "reservaciones";
    }
    
    // API para obtener la disponibilidad de franjas dinámicamente con JavaScript
    @GetMapping("/api/disponibilidad")
    @ResponseBody
    public List<FranjaDisponibleDTO> getDisponibilidad(@RequestParam String fecha) {
        LocalDate fechaSeleccionada = LocalDate.parse(fecha);
        return reservaService.getDisponibilidadFranjas(fechaSeleccionada);
    }
    
    // --- NUEVO ENDPOINT DE API ---
    @GetMapping("/api/disponibilidad-tipos")
    @ResponseBody
    public List<DisponibilidadTipoMesaDTO> getDisponibilidadTipos(@RequestParam String fecha, @RequestParam Integer idFranja) {
        LocalDate fechaSeleccionada = LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE);
        return reservaService.getDisponibilidadPorTipoMesa(fechaSeleccionada, idFranja);
    }

    @PostMapping("/reservaciones/crear")
    public String procesarReserva(@ModelAttribute("reservaForm") ReservaFormDTO formDTO,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        // Regla 1: El usuario debe estar registrado para hacer una reserva.
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            String username = authentication.getName();
            reservaService.crearReserva(formDTO, username);
            redirectAttributes.addFlashAttribute("successMessage", "¡Tu reserva ha sido confirmada exitosamente!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la reserva: " + e.getMessage());
        }
        
        return "redirect:/reservaciones";
    }
}