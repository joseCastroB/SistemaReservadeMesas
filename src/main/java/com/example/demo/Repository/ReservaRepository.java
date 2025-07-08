package com.example.demo.Repository;

import com.example.demo.Entities.Reserva;
import com.example.demo.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

// --- Repositorio de Reservas con consultas personalizadas ---
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>, JpaSpecificationExecutor<Reserva> {
    
    // Cuenta cuántas personas ya han reservado en una fecha y franja específicas.
    @Query("SELECT SUM(r.numeroPersonas) FROM Reserva r WHERE r.fecha = :fecha AND r.franja.idFranja = :idFranja AND r.estado = 'CONFIRMADA'")
    Integer countPersonasByFechaAndFranja(LocalDate fecha, Integer idFranja);
    
    // Cuenta cuántas mesas ya se han consumido en una fecha y franja.
    // --- CONSULTA CORREGIDA ---
    // Ahora usa CEILING para calcular correctamente las mesas consumidas.
    // Se añade 'nativeQuery = true' y se usan nombres de columna SQL (snake_case).
    @Query(value = "SELECT SUM(CEILING(r.numero_personas / 5.0)) FROM Reserva r WHERE r.fecha = :fecha AND r.id_franja = :idFranja AND r.estado = 'CONFIRMADA'", nativeQuery = true)
    Integer countMesasByFechaAndFranja(LocalDate fecha, Integer idFranja);

    // Busca si un usuario ya tiene una reserva para un día específico.
    List<Reserva> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);

    // --- NUEVO MÉTODO ---
    // Cuenta las mesas reservadas para un TIPO específico en una fecha y franja.
    // --- CONSULTA CORREGIDA ---
    // También se corrige aquí para la disponibilidad por tipo de mesa.
    // También se corrige aquí.
    @Query(value = "SELECT SUM(CEILING(r.numero_personas / 5.0)) FROM Reserva r WHERE r.fecha = :fecha AND r.id_franja = :idFranja AND r.id_tipo_mesa = :idTipoMesa AND r.estado = 'CONFIRMADA'", nativeQuery = true)
    Integer countMesasByFechaAndFranjaAndTipoMesa(LocalDate fecha, Integer idFranja, Integer idTipoMesa);

    // --- NUEVO MÉTODO ---
    // Busca todas las reservas para una fecha específica.
    List<Reserva> findByFecha(LocalDate fecha);

    // --- NUEVO MÉTODO PARA GRÁFICO DE ESTADO ---
    @Query("SELECT r.estado, COUNT(r) FROM Reserva r WHERE r.fecha BETWEEN :fechaInicio AND :fechaFin GROUP BY r.estado")
    List<Object[]> countReservasByEstado(LocalDate fechaInicio, LocalDate fechaFin);

    // --- NUEVO MÉTODO PARA GRÁFICO DE TIPO DE MESA ---
    @Query("SELECT tm.nombre, COUNT(r) FROM Reserva r JOIN r.tipoMesa tm WHERE r.fecha BETWEEN :fechaInicio AND :fechaFin GROUP BY tm.nombre")
    List<Object[]> countReservasByTipoMesa(LocalDate fechaInicio, LocalDate fechaFin);

}   
