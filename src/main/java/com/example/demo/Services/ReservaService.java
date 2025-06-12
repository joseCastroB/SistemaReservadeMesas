package com.example.demo.Services;

import com.example.demo.dto.FranjaDisponibleDTO;
import com.example.demo.dto.ReservaFormDTO;
import com.example.demo.Entities.ConfiguracionFranja;
import com.example.demo.Entities.Reserva;
import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.ConfiguracionFranjaRepository;
import com.example.demo.Repository.ReservaRepository;
import com.example.demo.Repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ConfiguracionFranjaRepository franjaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository, ConfiguracionFranjaRepository franjaRepository, UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.franjaRepository = franjaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene la disponibilidad de todas las franjas para una fecha dada.
     */
    public List<FranjaDisponibleDTO> getDisponibilidadFranjas(LocalDate fecha) {
        List<ConfiguracionFranja> franjas = franjaRepository.findAll();
        
        return franjas.stream().map(franja -> {
            Integer personasOcupadas = reservaRepository.countPersonasByFechaAndFranja(fecha, franja.getIdFranja());
            Integer mesasOcupadas = reservaRepository.countMesasByFechaAndFranja(fecha, franja.getIdFranja());
            
            // Si es null (no hay reservas), lo tratamos como 0.
            personasOcupadas = (personasOcupadas == null) ? 0 : personasOcupadas;
            mesasOcupadas = (mesasOcupadas == null) ? 0 : mesasOcupadas;
            
            int personasDisponibles = franja.getCapacidadMaxima() - personasOcupadas;
            int mesasDisponibles = franja.getCantidadMesas() - mesasOcupadas;
            
            // Regla del 70%
            boolean casiLleno = (double) personasOcupadas / franja.getCapacidadMaxima() >= 0.7;

            return new FranjaDisponibleDTO(franja.getIdFranja(), franja.getFranjaHoraria(), mesasDisponibles, personasDisponibles, casiLleno);
        }).collect(Collectors.toList());
    }

    /**
     * Crea una nueva reserva aplicando todas las reglas de negocio.
     */
    @Transactional
    public Reserva crearReserva(ReservaFormDTO formDTO, String username) {
        // Regla 1: El usuario debe estar registrado. Lo obtenemos.
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado."));

        LocalDate fechaReserva = LocalDate.parse(formDTO.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE);

        // Regla 1: El usuario solo puede tener una reserva por día.
        if (!reservaRepository.findByUsuarioAndFecha(usuario, fechaReserva).isEmpty()) {
            throw new IllegalStateException("Ya tienes una reserva para este día.");
        }
        
        // Regla 5 y 6: Validar disponibilidad.
        List<FranjaDisponibleDTO> disponibilidad = getDisponibilidadFranjas(fechaReserva);
        FranjaDisponibleDTO franjaSeleccionada = disponibilidad.stream()
                .filter(f -> f.getIdFranja().equals(formDTO.getIdFranja()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("La franja horaria no es válida."));

        int mesasRequeridas = formDTO.getNumeroPersonas() <= 5 ? 1 : 2;

        if (formDTO.getNumeroPersonas() > franjaSeleccionada.getPersonasDisponibles() || mesasRequeridas > franjaSeleccionada.getMesasDisponibles()) {
            throw new IllegalStateException("No hay suficiente disponibilidad para el número de personas o mesas en esta franja.");
        }
        
        // Si todas las validaciones pasan, creamos la reserva.
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setNombreCliente(formDTO.getNombreCliente());
        nuevaReserva.setCorreoCliente(formDTO.getCorreoCliente());
        nuevaReserva.setTelefonoCliente(formDTO.getTelefonoCliente());
        nuevaReserva.setComentarios(formDTO.getComentarios());
        nuevaReserva.setFecha(fechaReserva);
        nuevaReserva.setNumeroPersonas(formDTO.getNumeroPersonas());
        nuevaReserva.setEstado("CONFIRMADA");
        nuevaReserva.setUsuario(usuario);
        
        ConfiguracionFranja franja = franjaRepository.findById(formDTO.getIdFranja()).get();
        nuevaReserva.setFranja(franja);
        
        return reservaRepository.save(nuevaReserva);
    }
}