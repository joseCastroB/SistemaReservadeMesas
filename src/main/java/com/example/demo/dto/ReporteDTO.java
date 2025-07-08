package com.example.demo.dto;

import com.example.demo.Entities.Reserva;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReporteDTO {
    
    // La lista de reservas filtradas para mostrar en la tabla.
    private List<Reserva> reservas;
    
    // Un mapa para los datos del gráfico de estado. Ej: {"CONFIRMADA": 10, "SE PRESENTÓ": 5}
    private Map<String, Long> conteoPorEstado;
    
    // Un mapa para los datos del gráfico de tipo de mesa. Ej: {"Común": 15, "Terraza": 8}
    private Map<String, Long> conteoPorTipoMesa;
    
}
