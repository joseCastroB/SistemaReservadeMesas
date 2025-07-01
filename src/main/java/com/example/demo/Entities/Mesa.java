package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa una Mesa física del restaurante
 * 
 * Esta entidad representa las mesas físicas individuales del restaurante.
 * Cada mesa tiene una capacidad específica y pertenece a un tipo de mesa.
 * 
 * Aunque la entidad existe para completitud del modelo, el sistema
 * actualmente maneja las reservas de forma abstracta por tipo de mesa,
 * no por mesa individual específica.
 * 
 * Relaciones:
 * - @ManyToOne con TipoMesa: Cada mesa pertenece a un tipo específico
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Data
@Entity
@Table(name = "Mesa")
public class Mesa {
    
    /**
     * Identificador único de la mesa (clave primaria)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMesa;
    
    /**
     * Capacidad máxima de personas de la mesa
     * Define cuántas personas pueden sentarse cómodamente
     */
    private Integer capacidad;

    /**
     * Relación Many-to-One con TipoMesa
     * Cada mesa pertenece a un tipo específico (VIP, Familiar, etc.)
     * No puede ser null - toda mesa debe tener un tipo asignado
     */
    @ManyToOne
    @JoinColumn(name = "idTipoMesa", nullable = false)
    private TipoMesa tipoMesa;
}