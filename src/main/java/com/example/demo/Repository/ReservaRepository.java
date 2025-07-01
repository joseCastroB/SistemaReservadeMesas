package com.example.demo.Repository;

import com.example.demo.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad Reserva con consultas personalizadas
 * 
 * Esta interfaz extiende JpaRepository para operaciones CRUD básicas
 * y JpaSpecificationExecutor para consultas dinámicas complejas.
 * 
 * Funcionalidades:
 * - Operaciones CRUD automáticas (save, find, delete, etc.)
 * - Consultas personalizadas con @Query para lógica de negocio específica
 * - Soporte para Specifications para filtros dinámicos
 * - Consultas de agregación para calcular disponibilidad
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>, JpaSpecificationExecutor<Reserva> {
    
    /**
     * Cuenta el total de personas reservadas en una fecha y franja específicas
     * 
     * Esta consulta se usa para:
     * - Calcular la disponibilidad de personas en una franja
     * - Aplicar la regla del 70% de ocupación
     * - Validar que no se exceda la capacidad máxima
     * 
     * Solo cuenta reservas con estado 'CONFIRMADA' para excluir canceladas.
     * 
     * @param fecha Fecha de las reservas a contar
     * @param idFranja ID de la franja horaria
     * @return Suma total de personas reservadas, null si no hay reservas
     */
    @Query("SELECT SUM(r.numeroPersonas) FROM Reserva r WHERE r.fecha = :fecha AND r.franja.idFranja = :idFranja AND r.estado = 'CONFIRMADA'")
    Integer countPersonasByFechaAndFranja(LocalDate fecha, Integer idFranja);
    
    /**
     * Cuenta el total de mesas ocupadas en una fecha y franja específicas
     * 
     * Lógica de negocio aplicada:
     * - 1-5 personas = 1 mesa
     * - 6+ personas = 2 mesas
     * 
     * Esta consulta se usa para:
     * - Calcular cuántas mesas están ocupadas
     * - Determinar la disponibilidad de mesas por franja
     * - Validar límites de capacidad de mesas
     * 
     * @param fecha Fecha de las reservas a contar
     * @param idFranja ID de la franja horaria
     * @return Total de mesas ocupadas, null si no hay reservas
     */
    @Query("SELECT SUM(CASE WHEN r.numeroPersonas <= 5 THEN 1 ELSE 2 END) FROM Reserva r WHERE r.fecha = :fecha AND r.franja.idFranja = :idFranja AND r.estado = 'CONFIRMADA'")
    Integer countMesasByFechaAndFranja(LocalDate fecha, Integer idFranja);

    /**
     * Busca si un usuario ya tiene una reserva para un día específico
     * 
     * Implementa la regla de negocio: "Un usuario no puede tener
     * múltiples reservas en la misma fecha".
     * 
     * @param usuario Usuario a verificar
     * @param fecha Fecha a verificar
     * @return Lista de reservas (debería estar vacía o tener máximo 1 elemento)
     */
    List<Reserva> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);

    /**
     * Cuenta las mesas ocupadas para un TIPO específico de mesa
     * 
     * Esta consulta implementa el control de disponibilidad por tipo de mesa.
     * Cada tipo de mesa tiene 5 mesas disponibles como regla de negocio.
     * 
     * Permite controlar que no se exceda la disponibilidad de un tipo
     * específico (ej: no más de 5 reservas para mesas VIP).
     * 
     * @param fecha Fecha de las reservas
     * @param idFranja ID de la franja horaria
     * @param idTipoMesa ID del tipo de mesa específico
     * @return Número de mesas ocupadas de ese tipo, null si no hay reservas
     */
    @Query("SELECT SUM(CASE WHEN r.numeroPersonas <= 5 THEN 1 ELSE 2 END) FROM Reserva r WHERE r.fecha = :fecha AND r.franja.idFranja = :idFranja AND r.tipoMesa.idTipoMesa = :idTipoMesa AND r.estado = 'CONFIRMADA'")
    Integer countMesasByFechaAndFranjaAndTipoMesa(LocalDate fecha, Integer idFranja, Integer idTipoMesa);

    /**
     * Busca todas las reservas para una fecha específica
     * 
     * Usado en el panel administrativo para:
     * - Mostrar todas las reservas del día
     * - Generar reportes diarios
     * - Visualizar la ocupación general
     * 
     * @param fecha Fecha de las reservas a buscar
     * @return Lista de todas las reservas para esa fecha
     */
    List<Reserva> findByFecha(LocalDate fecha);
}
