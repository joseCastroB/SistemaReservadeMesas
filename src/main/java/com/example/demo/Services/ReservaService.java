package com.example.demo.Services;

import com.example.demo.dto.DisponibilidadTipoMesaDTO;
import com.example.demo.dto.FranjaDisponibleDTO;
import com.example.demo.dto.ReservaFormDTO;
import com.example.demo.Entities.ConfiguracionFranja;
import com.example.demo.Entities.Reserva;
import com.example.demo.Entities.TipoMesa;
import com.example.demo.Entities.Usuario;
import com.example.demo.Repository.ConfiguracionFranjaRepository;
import com.example.demo.Repository.ReservaRepository;
import com.example.demo.Repository.TipoMesaRepository;
import com.example.demo.Repository.UsuarioRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate; // Importante
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ConfiguracionFranjaRepository franjaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoMesaRepository tipoMesaRepository; // Añadir

    public ReservaService(ReservaRepository reservaRepository, ConfiguracionFranjaRepository franjaRepository, UsuarioRepository usuarioRepository, TipoMesaRepository tipoMesaRepository) {
        this.reservaRepository = reservaRepository;
        this.franjaRepository = franjaRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoMesaRepository = tipoMesaRepository;
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


    // --- NUEVO MÉTODO ---
    /**
     * Obtiene la disponibilidad detallada para cada tipo de mesa en una fecha y franja específicas.
     */
    public List<DisponibilidadTipoMesaDTO> getDisponibilidadPorTipoMesa(LocalDate fecha, Integer idFranja) {
        List<TipoMesa> todosLosTipos = tipoMesaRepository.findAll();
        final int MESAS_POR_TIPO = 5; // Regla de negocio: 5 mesas por cada tipo

        return todosLosTipos.stream().map(tipo -> {
            Integer mesasOcupadas = reservaRepository.countMesasByFechaAndFranjaAndTipoMesa(fecha, idFranja, tipo.getIdTipoMesa());
            mesasOcupadas = (mesasOcupadas == null) ? 0 : mesasOcupadas;
            
            int mesasDisponibles = MESAS_POR_TIPO - mesasOcupadas;
            
            return new DisponibilidadTipoMesaDTO(tipo.getIdTipoMesa(), tipo.getNombre(), mesasDisponibles);
        }).collect(Collectors.toList());
    }

    /**
     * MÉTODO CORREGIDO: Crea una nueva reserva aplicando todas las reglas de negocio,
     * incluyendo la validación de disponibilidad por tipo de mesa y la regla del 70%.
     */
    @Transactional
    public Reserva crearReserva(ReservaFormDTO formDTO, String username) {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado."));

        LocalDate fechaReserva = LocalDate.parse(formDTO.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE);

        if (!reservaRepository.findByUsuarioAndFecha(usuario, fechaReserva).isEmpty()) {
            throw new IllegalStateException("Ya tienes una reserva para este día.");
        }
        
        // --- INICIO DE LA VALIDACIÓN CORREGIDA ---
        
        // 1. Obtener la franja y calcular ocupación actual
        ConfiguracionFranja franja = franjaRepository.findById(formDTO.getIdFranja())
                .orElseThrow(() -> new IllegalStateException("Franja no encontrada"));
        Integer personasOcupadas = reservaRepository.countPersonasByFechaAndFranja(fechaReserva, formDTO.getIdFranja());
        personasOcupadas = (personasOcupadas == null) ? 0 : personasOcupadas;
        
        // 2. VALIDACIÓN CLAVE: Regla del 70%
        boolean estaCasiLleno = (double) personasOcupadas / franja.getCapacidadMaxima() >= 0.7;
        if (estaCasiLleno) {
            throw new IllegalStateException("Esta franja horaria ya está casi llena y no acepta más reservas en este momento.");
        }

        // 3. Calcular mesas requeridas por el usuario
        int mesasRequeridas = formDTO.getNumeroPersonas() <= 5 ? 1 : 2;
        final int MESAS_INICIALES_POR_TIPO = 5;

        // 4. Contar mesas ya ocupadas para el TIPO de mesa específico
        Integer mesasOcupadasDelTipo = reservaRepository.countMesasByFechaAndFranjaAndTipoMesa(
            fechaReserva, formDTO.getIdFranja(), formDTO.getIdTipoMesa()
        );
        mesasOcupadasDelTipo = (mesasOcupadasDelTipo == null) ? 0 : mesasOcupadasDelTipo;
        int mesasDisponiblesDelTipo = MESAS_INICIALES_POR_TIPO - mesasOcupadasDelTipo;

        // 5. VALIDACIÓN de mesas por tipo
        if (mesasRequeridas > mesasDisponiblesDelTipo) {
            throw new IllegalStateException("No hay suficientes mesas del tipo seleccionado para el número de personas.");
        }

        // 6. Validación de capacidad total de personas (se mantiene como una segunda barrera)
        if ((personasOcupadas + formDTO.getNumeroPersonas()) > franja.getCapacidadMaxima()) {
            throw new IllegalStateException("La capacidad máxima de personas para esta franja sería excedida.");
        }
        
        // --- FIN DE LA VALIDACIÓN CORREGIDA ---
        
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
        nuevaReserva.setFranja(franja);
        
        TipoMesa tipoMesaSeleccionado = tipoMesaRepository.findById(formDTO.getIdTipoMesa())
                .orElseThrow(() -> new IllegalStateException("Tipo de mesa no válido."));
        nuevaReserva.setTipoMesa(tipoMesaSeleccionado);
        
        return reservaRepository.save(nuevaReserva);
    }

    // --- NUEVO MÉTODO ---
    public List<Reserva> findReservasByFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha);
    }

    // --- NUEVO MÉTODO ---
    @Transactional
    public void actualizarEstadoReserva(Integer idReserva, String nuevoEstado) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalStateException("Reserva no encontrada con ID: " + idReserva));
        
        reserva.setEstado(nuevoEstado);
        reservaRepository.save(reserva);
    }

    /**
     * NUEVO MÉTODO: Encuentra reservas usando una combinación de filtros opcionales.
     * Utiliza JPA Specifications para construir la consulta dinámicamente.
     */
    public List<Reserva> findWithFilters(LocalDate fecha, String nombre, Integer idTipoMesa, Integer idFranja, String estado) {
        return reservaRepository.findAll((Specification<Reserva>) (root, query, criteriaBuilder) -> {
            
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro obligatorio por fecha
            predicates.add(criteriaBuilder.equal(root.get("fecha"), fecha));

            // 2. Filtros opcionales
            if (nombre != null && !nombre.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreCliente")), "%" + nombre.toLowerCase() + "%"));
            }
            if (idTipoMesa != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoMesa").get("idTipoMesa"), idTipoMesa));
            }
            if (idFranja != null) {
                predicates.add(criteriaBuilder.equal(root.get("franja").get("idFranja"), idFranja));
            }
            if (estado != null && !estado.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            // Ordena por ID para mantener un orden consistente
            query.orderBy(criteriaBuilder.asc(root.get("idReserva")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    /**
     * NUEVO MÉTODO: Crea una reserva anónima (realizada por un admin).
     * No tiene la restricción de una reserva por día por usuario.
     */
    @Transactional
    public Reserva crearReservaAnonima(ReservaFormDTO formDTO, String adminUsername) {
        // El admin que realiza la acción
        Usuario admin = usuarioRepository.findByUsuario(adminUsername)
                .orElseThrow(() -> new IllegalStateException("Administrador no autenticado."));

        LocalDate fechaReserva = LocalDate.parse(formDTO.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE);
        
        // --- VALIDACIONES DE DISPONIBILIDAD (similares a la reserva normal) ---
        ConfiguracionFranja franja = franjaRepository.findById(formDTO.getIdFranja())
                .orElseThrow(() -> new IllegalStateException("Franja no encontrada"));
        Integer personasOcupadas = reservaRepository.countPersonasByFechaAndFranja(fechaReserva, formDTO.getIdFranja());
        personasOcupadas = (personasOcupadas == null) ? 0 : personasOcupadas;

        // Validamos la capacidad total de personas
        if ((personasOcupadas + formDTO.getNumeroPersonas()) > franja.getCapacidadMaxima()) {
            throw new IllegalStateException("La capacidad máxima de personas para esta franja sería excedida.");
        }

        // Validamos la disponibilidad por tipo de mesa
        int mesasRequeridas = (int) Math.ceil((double) formDTO.getNumeroPersonas() / 5.0);
        final int MESAS_INICIALES_POR_TIPO = 5;
        Integer mesasOcupadasDelTipo = reservaRepository.countMesasByFechaAndFranjaAndTipoMesa(
            fechaReserva, formDTO.getIdFranja(), formDTO.getIdTipoMesa()
        );
        mesasOcupadasDelTipo = (mesasOcupadasDelTipo == null) ? 0 : mesasOcupadasDelTipo;
        int mesasDisponiblesDelTipo = MESAS_INICIALES_POR_TIPO - mesasOcupadasDelTipo;

        if (mesasRequeridas > mesasDisponiblesDelTipo) {
            throw new IllegalStateException("No hay suficientes mesas del tipo seleccionado para el número de personas.");
        }
        
        // --- Creación de la reserva ---
        Reserva nuevaReserva = new Reserva();
        // Usamos los datos por defecto para el cliente anónimo
        nuevaReserva.setNombreCliente("Anónimo");
        nuevaReserva.setCorreoCliente("anonimo@sietesopas.com");
        nuevaReserva.setTelefonoCliente("999999999");
        nuevaReserva.setComentarios("Reserva en local por admin: " + adminUsername); // Comentario por defecto
        
        nuevaReserva.setFecha(fechaReserva);
        nuevaReserva.setNumeroPersonas(formDTO.getNumeroPersonas());
        nuevaReserva.setEstado("CONFIRMADA"); // Las reservas en local nacen con estado "CONFIRMADA"
        nuevaReserva.setUsuario(null); // La reserva no pertenece a un cliente, sino que fue gestionada por un admin
        nuevaReserva.setFranja(franja);
        
        TipoMesa tipoMesaSeleccionado = tipoMesaRepository.findById(formDTO.getIdTipoMesa())
                .orElseThrow(() -> new IllegalStateException("Tipo de mesa no válido."));
        nuevaReserva.setTipoMesa(tipoMesaSeleccionado);
        
        return reservaRepository.save(nuevaReserva);
    }


}