package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private LocalDate fecha;
    private Integer numeroPersonas;
    private String estado; // Ej: "CONFIRMADA", "CANCELADA"
    private String comentarios;

    @ManyToOne
    @JoinColumn(name = "idFranja", nullable = false)
    private ConfiguracionFranja franja;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
    
    // Nota: El idMesa se manejará de forma más abstracta en la lógica de servicio,
    // ya que una reserva puede ocupar más de una mesa.

    // --- LÍNEA AÑADIDA ---
    // Ahora cada reserva está asociada a un tipo de mesa.
    @ManyToOne
    @JoinColumn(name = "idTipoMesa", nullable = false)
    private TipoMesa tipoMesa;

}