package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entidad JPA que representa una Reserva en el sistema
 * 
 * Esta es la entidad central del sistema de reservas.
 * Almacena toda la información necesaria para gestionar las reservas de mesas,
 * incluyendo datos del cliente, fecha, capacidad y relaciones con otras entidades.
 * 
 * Relaciones JPA:
 * - @ManyToOne con ConfiguracionFranja: Una reserva pertenece a una franja horaria
 * - @ManyToOne con Usuario: Una reserva pertenece a un usuario registrado
 * - @ManyToOne con TipoMesa: Una reserva está asociada a un tipo específico de mesa
 * 
 * Estados posibles:
 * - "CONFIRMADA": Reserva activa y válida
 * - "CANCELADA": Reserva cancelada por el administrador
 * - "COMPLETADA": Reserva finalizada (cliente ya asistió)
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Data
@Entity
@Table(name = "Reserva")
public class Reserva {
    
    /**
     * Identificador único de la reserva (clave primaria)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;
    
    /**
     * Nombre completo del cliente que realiza la reserva
     * Se puede autocompletar desde los datos del usuario registrado
     */
    private String nombreCliente;
    
    /**
     * Correo electrónico del cliente
     * Usado para enviar confirmaciones (futuro)
     */
    private String correoCliente;
    
    /**
     * Teléfono de contacto del cliente
     * Campo requerido para contacto directo
     */
    private String telefonoCliente;
    
    /**
     * Fecha de la reserva
     * No puede ser anterior a la fecha actual
     */
    private LocalDate fecha;
    
    /**
     * Número de personas para la reserva
     * Determina cuántas mesas se necesitan:
     * - 1-5 personas: 1 mesa
     * - 6+ personas: 2 mesas
     */
    private Integer numeroPersonas;
    
    /**
     * Estado actual de la reserva
     * Valores: "CONFIRMADA", "CANCELADA", "COMPLETADA"
     */
    private String estado; // Ej: "CONFIRMADA", "CANCELADA"
    
    /**
     * Comentarios adicionales del cliente
     * Campo opcional para solicitudes especiales
     */
    private String comentarios;

    /**
     * Relación Many-to-One con ConfiguracionFranja
     * Una reserva pertenece a una franja horaria específica
     * No puede ser null - toda reserva debe tener una franja asignada
     */
    @ManyToOne
    @JoinColumn(name = "idFranja", nullable = false)
    private ConfiguracionFranja franja;

    /**
     * Relación Many-to-One con Usuario
     * Una reserva pertenece al usuario que la creó
     * Puede ser null para reservas anónimas (futuro)
     */
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
    
    // Nota: El idMesa se manejará de forma más abstracta en la lógica de servicio,
    // ya que una reserva puede ocupar más de una mesa.

    /**
     * Relación Many-to-One con TipoMesa
     * Cada reserva está asociada a un tipo específico de mesa
     * Permite controlar la disponibilidad por categoría de mesa
     * No puede ser null - toda reserva debe especificar el tipo de mesa
     */
    @ManyToOne
    @JoinColumn(name = "idTipoMesa", nullable = false)
    private TipoMesa tipoMesa;

}