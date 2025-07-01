# Resumen de Documentación - Sistema de Reserva de Mesas "7 Sopas"

## 📚 Archivos de Documentación Creados

### 1. **DOCUMENTACION_COMPLETA.md**
   - **Descripción general del proyecto**
   - **Arquitectura y patrones de diseño (MVC, DTO, DAO)**
   - **Estructura completa del proyecto**
   - **Flujo de funciones principales**
   - **Configuración de seguridad**
   - **Tecnologías utilizadas**

### 2. **FLUJO_DATOS_COMUNICACION.md**
   - **Arquitectura de capas detallada**
   - **Comunicación entre componentes**
   - **Flujo completo de creación de reservas**
   - **APIs REST para consultas dinámicas**
   - **Proceso de autenticación**
   - **Gestión de transacciones**

### 3. **DOCUMENTACION_BASE_DATOS.md**
   - **Configuración H2 Database**
   - **Esquema completo de base de datos**
   - **Definición de todas las tablas**
   - **Consultas principales del sistema**
   - **Optimización y performance**
   - **Seguridad de datos**

### 4. **GUIA_ANOTACIONES_TECNOLOGIAS.md** ⭐ **NUEVO**
   - **Explicación detallada de todas las anotaciones**
   - **Qué hace cada función de Spring, JPA, Lombok**
   - **Ejemplos prácticos y comparaciones**
   - **Guía para desarrolladores sin experiencia en estas tecnologías**

---

## 🏗️ Patrones de Diseño Implementados

### **Patrón MVC (Model-View-Controller)**

#### **Model (Modelo)**
```java
// Entidades JPA con anotaciones completas
@Entity
@Table(name = "Usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    // ... propiedades con documentación
}
```

#### **View (Vista)**
- Templates Thymeleaf en `src/main/resources/templates/`
- Archivos estáticos en `src/main/resources/static/`
- DTOs para transferencia de datos de formularios

#### **Controller (Controlador)**
```java
@Controller
public class ReservaController {
    // Gestión de peticiones HTTP
    // Validación de autenticación
    // Delegación a capa de servicio
}
```

### **Patrón DTO (Data Transfer Object)**
```java
@Data
public class ReservaFormDTO {
    // Campos específicos para formularios
    // Validaciones de entrada
    // Conversión de tipos de datos
}
```

### **Patrón Repository/DAO**
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Métodos de consulta derivados
    // Consultas personalizadas con @Query
    Optional<Usuario> findByUsuario(String usuario);
}
```

### **Patrón Service Layer**
```java
@Service
public class ReservaService {
    // Lógica de negocio centralizada
    // Validaciones complejas
    // Gestión de transacciones
}
```

---

## 🔄 Flujo Principal de Funciones

### **1. Registro de Usuario**
```
Formulario → AuthController → UsuarioService → UsuarioRepository → Base de Datos
    ↓              ↓               ↓               ↓                    ↓
Validación    Conversión     Lógica de      Persistencia        Almacenamiento
 Frontend      DTO→Entity    Negocio        JPA                 H2 Database
```

### **2. Autenticación**
```
Login → SecurityConfig → CustomUserDetailsService → UsuarioRepository
  ↓           ↓                    ↓                      ↓
Credenciales  Spring Security    UserDetails          Base de Datos
```

### **3. Creación de Reserva**
```
Formulario → ReservaController → ReservaService → ReservaRepository
    ↓              ↓                    ↓               ↓
  DTO         Validación         Reglas de Negocio   Persistencia
              Autenticación      - Regla 70%         JPA Queries
                                - Disponibilidad
                                - Una por día
```

---

## 🗄️ Manejo de Base de Datos

### **Configuración H2**
```properties
# Base de datos persistente en archivo
spring.datasource.url=jdbc:h2:file:./sietesopasdb
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

### **Entidades Principales**
1. **Usuario**: Gestión de usuarios y autenticación
2. **Reserva**: Entidad central del sistema
3. **TipoMesa**: Categorías de mesas (VIP, Familiar, etc.)
4. **ConfiguracionFranja**: Horarios y capacidades
5. **Mesa**: Mesas físicas individuales

### **Consultas Clave**
```sql
-- Calcular ocupación por franja
SELECT SUM(numeroPersonas) FROM Reserva 
WHERE fecha = ? AND idFranja = ? AND estado = 'CONFIRMADA';

-- Contar mesas por tipo
SELECT SUM(CASE WHEN numeroPersonas <= 5 THEN 1 ELSE 2 END) 
FROM Reserva WHERE fecha = ? AND idFranja = ? AND idTipoMesa = ?;
```

---

## 🔐 Seguridad

### **Spring Security**
- **Autenticación**: BCrypt para contraseñas
- **Autorización**: Roles USER y ADMIN
- **Protección CSRF**: Habilitada con excepciones específicas
- **Sesiones**: Gestión automática por Spring

### **Validaciones de Negocio**
```java
// Regla del 70%
boolean estaCasiLleno = (double) personasOcupadas / franja.getCapacidadMaxima() >= 0.7;

// Una reserva por usuario por día
if (!reservaRepository.findByUsuarioAndFecha(usuario, fechaReserva).isEmpty()) {
    throw new IllegalStateException("Ya tienes una reserva para este día.");
}
```

---

## 📊 Reglas de Negocio Implementadas

### **1. Regla del 70%**
Si una franja alcanza el 70% de capacidad, no acepta más reservas.

### **2. Control por Tipo de Mesa**
Cada tipo tiene 5 mesas disponibles máximo.

### **3. Cálculo de Mesas Requeridas**
- 1-5 personas = 1 mesa
- 6+ personas = 2 mesas

### **4. Estados de Reserva**
- **CONFIRMADA**: Reserva activa
- **CANCELADA**: Cancelada por admin
- **COMPLETADA**: Finalizada

---

## � Código Comentado

### **Todas las clases principales incluyen:**
- **Documentación técnica completa**
- **Explicación detallada de anotaciones Spring, JPA, Lombok**
- **Descripción de responsabilidades**
- **Descripción de patrones implementados**
- **Comentarios sobre lógica de negocio**
- **Eliminadas referencias de @author, @version, @since**

### **Explicaciones agregadas para anotaciones clave:**

#### **Lombok**
```java
@Data  // Genera getters, setters, toString, equals, hashCode automáticamente
```

#### **JPA (Java Persistence API)**
```java
@Entity                                    // Marca clase como tabla de BD
@Table(name = "Usuarios")                 // Nombre específico de tabla
@Id                                       // Clave primaria
@GeneratedValue(strategy = IDENTITY)      // Auto-incremento
@Column(unique = true, nullable = false)  // Restricciones de columna
@ManyToOne                               // Relación muchos a uno
```

#### **Spring Framework**
```java
@Service        // Lógica de negocio
@Repository     // Acceso a datos
@Controller     // Manejo de peticiones web
@GetMapping     // Endpoint HTTP GET
@PostMapping    // Endpoint HTTP POST
@Transactional  // Operaciones atómicas de BD
```

---

## 📈 Tecnologías y Dependencias

### **Backend**
- **Spring Boot 3.4.4**: Framework principal
- **Spring Data JPA**: Persistencia
- **Spring Security**: Autenticación/Autorización
- **H2 Database**: Base de datos embebida
- **Lombok**: Reducir boilerplate
- **Maven**: Gestión de dependencias

### **Frontend**
- **Thymeleaf**: Motor de templates
- **HTML5/CSS3**: Estructura y estilos
- **JavaScript**: Interactividad (APIs REST)

---

## 🎯 Documentación Completa

El proyecto ahora cuenta con:

✅ **Documentación arquitectural completa**
✅ **Explicación detallada de patrones de diseño**
✅ **Flujo de datos documentado**
✅ **Código completamente comentado**
✅ **Reglas de negocio explicadas**
✅ **Configuración de base de datos detallada**
✅ **Ejemplos de consultas y APIs**
✅ **Medidas de seguridad documentadas**

La documentación está organizada en archivos separados para facilitar la lectura y mantenimiento, cubriendo todos los aspectos técnicos y de negocio del sistema.
