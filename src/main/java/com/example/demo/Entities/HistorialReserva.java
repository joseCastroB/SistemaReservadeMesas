package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "HistorialReserva")
public class HistorialReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorial;

    @ManyToOne
    @JoinColumn(name = "idReserva", nullable = false)
    private Reserva reserva;
    
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
    
    private LocalDateTime fechaHora;
}
