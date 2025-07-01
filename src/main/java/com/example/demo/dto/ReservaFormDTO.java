package com.example.demo.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) para el formulario de creación de reservas
 * 
 * Este DTO implementa el patrón Data Transfer Object para transferir
 * datos entre la capa de presentación (formulario HTML) y la capa de servicio.
 * 
 * Ventajas del patrón DTO:
 * - Desacopla el formulario de las entidades JPA
 * - Permite validaciones específicas para formularios
 * - Facilita el manejo de datos de entrada del usuario
 * - Evita exponer la estructura interna de las entidades
 * 
 * Conversión de datos:
 * - La fecha se recibe como String desde el formulario HTML
 * - Se convierte a LocalDate en el servicio usando DateTimeFormatter
 * - Los IDs de franja y tipo de mesa se validan en el servicio
 * 
 * @author Sistema 7 Sopas
 * @version 1.0
 * @since 2024
 */
@Data
public class ReservaFormDTO {
    
    /**
     * Nombre completo del cliente
     * Se autocompletará con los datos del usuario si está autenticado
     */
    private String nombreCliente;
    
    /**
     * Correo electrónico del cliente
     * Se autocompletará con los datos del usuario si está autenticado
     */
    private String correoCliente;
    
    /**
     * Teléfono de contacto del cliente
     * Se autocompletará con los datos del usuario si está autenticado
     */
    private String telefonoCliente;
    
    /**
     * Comentarios adicionales para la reserva
     * Campo opcional para solicitudes especiales
     */
    private String comentarios;
    
    /**
     * Fecha de la reserva en formato String
     * Se recibe desde el input type="date" del formulario HTML
     * Formato esperado: "YYYY-MM-DD" (ISO 8601)
     * Se convierte a LocalDate en ReservaService.crearReserva()
     */
    private String fecha; // Se recibe como String y se convierte
    
    /**
     * Número de personas para la reserva
     * Determina cuántas mesas se necesitan:
     * - 1-5 personas: 1 mesa
     * - 6+ personas: 2 mesas
     */
    private Integer numeroPersonas;
    
    /**
     * ID de la franja horaria seleccionada
     * Referencia a ConfiguracionFranja.idFranja
     * Se valida que exista en la base de datos
     */
    private Integer idFranja;
    
    /**
     * ID del tipo de mesa seleccionado
     * Referencia a TipoMesa.idTipoMesa
     * Se valida que exista y tenga mesas disponibles
     */
    private Integer idTipoMesa;
}