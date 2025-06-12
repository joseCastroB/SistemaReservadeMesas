package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para transportar información de disponibilidad de una franja horaria
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FranjaDisponibleDTO {
    private Integer idFranja;
    private String franjaHoraria;
    private Integer mesasDisponibles;
    private Integer personasDisponibles;
    private boolean estaCasiLleno; // Para la regla del 70%
}