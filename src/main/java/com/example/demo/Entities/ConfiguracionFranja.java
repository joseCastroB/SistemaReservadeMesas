package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ConfiguracionFranja")
public class ConfiguracionFranja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFranja;
    private String franjaHoraria; // Ej: "10:00 - 12:00"
    private Integer capacidadMaxima; // Ej: 100 personas
    private Integer cantidadMesas; // Ej: 20 mesas
}