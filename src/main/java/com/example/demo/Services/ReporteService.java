package com.example.demo.Services;

import com.example.demo.dto.ReporteDTO;
import com.example.demo.Entities.Reserva;
import com.example.demo.Repository.ReservaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service; // Importante que esté la anotación @Service
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service // ¡Esta anotación es crucial! Le dice a Spring que cree el bean.
public class ReporteService {
    
    private final ReservaRepository reservaRepository;

    public ReporteService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public ReporteDTO generarReporte(LocalDate fechaInicio, LocalDate fechaFin, String estado, Integer idTipoMesa) {
        
        // 1. Obtener la lista de reservas filtradas para la tabla
        List<Reserva> reservasFiltradas = findReservasForReport(fechaInicio, fechaFin, estado, idTipoMesa);

        // 2. Obtener los datos agregados para los gráficos (usando el rango de fechas, sin los otros filtros)
        Map<String, Long> conteoPorEstado = reservaRepository.countReservasByEstado(fechaInicio, fechaFin)
                .stream()
                .collect(Collectors.toMap(obj -> (String) obj[0], obj -> (Long) obj[1]));

        Map<String, Long> conteoPorTipoMesa = reservaRepository.countReservasByTipoMesa(fechaInicio, fechaFin)
                .stream()
                .collect(Collectors.toMap(obj -> (String) obj[0], obj -> (Long) obj[1]));
        
        // 3. Ensamblar y devolver el DTO
        ReporteDTO reporte = new ReporteDTO();
        reporte.setReservas(reservasFiltradas);
        reporte.setConteoPorEstado(conteoPorEstado);
        reporte.setConteoPorTipoMesa(conteoPorTipoMesa);
        
        return reporte;
    }
    
    private List<Reserva> findReservasForReport(LocalDate fechaInicio, LocalDate fechaFin, String estado, Integer idTipoMesa) {
        return reservaRepository.findAll((Specification<Reserva>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filtro por rango de fechas
            predicates.add(criteriaBuilder.between(root.get("fecha"), fechaInicio, fechaFin));
            
            // Filtros opcionales
            if (estado != null && !estado.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (idTipoMesa != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoMesa").get("idTipoMesa"), idTipoMesa));
            }
            
            query.orderBy(criteriaBuilder.desc(root.get("fecha")));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

}
