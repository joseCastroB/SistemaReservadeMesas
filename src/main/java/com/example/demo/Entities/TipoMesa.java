package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Entidad JPA que representa los diferentes tipos de mesas disponibles
 * 
 * Esta entidad define las categorías de mesas que maneja el restaurante,
 * como mesas VIP, familiares, de terraza, etc. Cada tipo tiene características
 * específicas y un número limitado de mesas disponibles.
 * 
 * Reglas de negocio:
 * - Cada tipo de mesa tiene 5 mesas físicas disponibles
 * - Los tipos se usan para controlar la disponibilidad específica
 * - Las reservas se asignan por tipo, no por mesa individual
 * 
 * Relaciones:
 * - Un TipoMesa puede tener múltiples Mesas físicas (1:N)
 * - Un TipoMesa puede tener múltiples Reservas (1:N)
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Data
@Entity
@Table(name = "TipoMesa")
public class TipoMesa {
    
    /**
     * Identificador único del tipo de mesa (clave primaria)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoMesa;
    
    /**
     * Nombre del tipo de mesa
     * Ejemplos: "VIP", "Familiar", "Terraza", "Estándar"
     */
    private String nombre;
    
    /**
     * Descripción detallada del tipo de mesa
     * Incluye características especiales y ubicación
     */
    private String descripcion;

    /**
     * Relación One-to-Many con Mesa
     * Lista de todas las mesas físicas de este tipo
     * Mapeada por el campo "tipoMesa" en la entidad Mesa
     * 
     * Nota: Aunque existe la relación, el sistema maneja
     * la disponibilidad de forma abstracta (5 mesas por tipo)
     */
    @OneToMany(mappedBy = "tipoMesa")
    private List<Mesa> mesas;
}
