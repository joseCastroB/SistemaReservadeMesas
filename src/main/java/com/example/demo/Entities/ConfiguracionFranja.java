package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa la configuración de franjas horarias
 * 
 * Esta entidad define los horarios disponibles para reservas y sus limitaciones.
 * Cada franja tiene una capacidad máxima de personas y mesas que controla
 * cuántas reservas se pueden aceptar en ese horario específico.
 * 
 * La regla del 70% se aplica sobre la capacidadMaxima:
 * - Si se alcanza el 70% de capacidad, no se aceptan más reservas
 * - Esto permite un margen de seguridad para walk-ins y contingencias
 * 
 * Relaciones:
 * - Una ConfiguracionFranja puede tener múltiples Reservas (1:N)
 * 
 * Anotaciones utilizadas:
 * - @Data: (Lombok) Genera automáticamente getters, setters, toString, equals, hashCode
 * - @Entity: (JPA) Marca esta clase como una tabla en la base de datos
 * - @Table: (JPA) Especifica el nombre exacto de la tabla "ConfiguracionFranja"
 */
@Data
@Entity
@Table(name = "ConfiguracionFranja")
public class ConfiguracionFranja {
    
    /**
     * Identificador único de la franja horaria (clave primaria)
     * 
     * @Id: (JPA) Marca este campo como clave primaria de la tabla
     * @GeneratedValue: (JPA) La base de datos genera automáticamente el valor
     * strategy = IDENTITY: Usa AUTO_INCREMENT de la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFranja;
    
    /**
     * Descripción del horario de la franja
     * Formato esperado: "HH:MM - HH:MM"
     * Ejemplo: "10:00 - 12:00", "12:30 - 14:30"
     */
    private String franjaHoraria; // Ej: "10:00 - 12:00"
    
    /**
     * Capacidad máxima de personas para esta franja
     * Se usa para aplicar la regla del 70% de ocupación
     * Ejemplo: 100 personas
     */
    private Integer capacidadMaxima; // Ej: 100 personas
    
    /**
     * Cantidad total de mesas disponibles en esta franja
     * Representa el total de mesas que el restaurante puede
     * atender simultáneamente en este horario
     * Ejemplo: 20 mesas
     */
    private Integer cantidadMesas; // Ej: 20 mesas
}