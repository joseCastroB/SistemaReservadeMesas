package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisponibilidadTipoMesaDTO {
    private Integer idTipoMesa;
    private String nombreTipoMesa;
    private Integer mesasDisponibles;
}
