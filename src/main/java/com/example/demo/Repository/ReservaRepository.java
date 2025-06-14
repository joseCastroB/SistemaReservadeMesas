package com.example.demo.Repository;

import com.example.demo.Entities.*;
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
    // Asume que 1-5 personas = 1 mesa, 6-10 = 2 mesas.
    @Query("SELECT SUM(CASE WHEN r.numeroPersonas <= 5 THEN 1 ELSE 2 END) FROM Reserva r WHERE r.fecha = :fecha AND r.franja.idFranja = :idFranja AND r.estado = 'CONFIRMADA'")
    Integer countMesasByFechaAndFranja(LocalDate fecha, Integer idFranja);

    // Busca si un usuario ya tiene una reserva para un día específico.
    List<Reserva> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);

    // --- NUEVO MÉTODO ---
    // Cuenta las mesas reservadas para un TIPO específico en una fecha y franja.
    @Query("SELECT SUM(CASE WHEN r.numeroPersonas <= 5 THEN 1 ELSE 2 END) FROM Reserva r WHERE r.fecha = :fecha AND r.franja.idFranja = :idFranja AND r.tipoMesa.idTipoMesa = :idTipoMesa AND r.estado = 'CONFIRMADA'")
    Integer countMesasByFechaAndFranjaAndTipoMesa(LocalDate fecha, Integer idFranja, Integer idTipoMesa);

    // --- NUEVO MÉTODO ---
    // Busca todas las reservas para una fecha específica.
    List<Reserva> findByFecha(LocalDate fecha);
}
