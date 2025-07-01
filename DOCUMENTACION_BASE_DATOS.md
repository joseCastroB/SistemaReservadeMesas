# Documentación - Manejo de Base de Datos

## 🗄️ Configuración de Base de Datos

### Tipo de Base de Datos: H2 Database

**H2** es una base de datos Java embebida que se utiliza tanto para desarrollo como para producción ligera.

#### Configuración (`application.properties`):
```properties
# Base de Datos H2 Persistente (en Archivo)
spring.datasource.url=jdbc:h2:file:./sietesopasdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Consola Web H2 (para desarrollo)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Pool de conexiones HikariCP
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.hikari.maximum-pool-size=5
```

#### Características de la Configuración:

1. **Persistencia en Archivo**: Los datos se almacenan en `sietesopasdb.mv.db`
2. **Consola Web**: Accesible en `http://localhost:8080/h2-console`
3. **DDL Auto-Update**: Hibernate actualiza automáticamente el esquema
4. **Pool de Conexiones**: Máximo 5 conexiones simultáneas

---

## 📊 Esquema de Base de Datos

### Diagrama Entidad-Relación

```
    ┌─────────────────┐         ┌─────────────────┐
    │    Usuarios     │         │  TipoMesa       │
    │─────────────────│         │─────────────────│
    │ idUsuario (PK)  │         │ idTipoMesa (PK) │
    │ nombreCompleto  │         │ nombre          │
    │ correo (UNIQUE) │         │ descripcion     │
    │ telefono        │         └─────────────────┘
    │ usuario (UNIQUE)│                 │
    │ contrasena      │                 │ 1
    │ rol             │                 │
    └─────────────────┘                 │
            │ 1                         │
            │                           │
            │                           │ N
            │ N               ┌─────────────────┐
    ┌─────────────────┐       │     Mesa        │
    │    Reserva      │       │─────────────────│
    │─────────────────│       │ idMesa (PK)     │
    │ idReserva (PK)  │       │ idTipoMesa (FK) │
    │ nombreCliente   │       │ capacidad       │
    │ correoCliente   │       └─────────────────┘
    │ telefonoCliente │
    │ fecha           │       ┌─────────────────┐
    │ numeroPersonas  │       │ConfiguracionFranja│
    │ estado          │       │─────────────────│
    │ comentarios     │   N   │ idFranja (PK)   │ 1
    │ idFranja (FK)   │────── │ franjaHoraria   │
    │ idUsuario (FK)  │       │ capacidadMaxima │
    │ idTipoMesa (FK) │       │ cantidadMesas   │
    └─────────────────┘       └─────────────────┘
```

---

## 📋 Definición de Tablas

### 1. Tabla `Usuarios`
```sql
CREATE TABLE Usuarios (
  idUsuario INT AUTO_INCREMENT PRIMARY KEY,
  nombreCompleto VARCHAR(255) NOT NULL,
  correo VARCHAR(255) UNIQUE NOT NULL,
  telefono VARCHAR(50),
  usuario VARCHAR(50) UNIQUE NOT NULL,
  contrasena VARCHAR(255) NOT NULL,
  rol VARCHAR(50) NOT NULL
);
```

**Propósito**: Almacena información de usuarios del sistema
**Índices**: 
- PRIMARY KEY en `idUsuario`
- UNIQUE en `correo`
- UNIQUE en `usuario`

### 2. Tabla `TipoMesa`
```sql
CREATE TABLE TipoMesa (
    idTipoMesa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);
```

**Propósito**: Define categorías de mesas (VIP, Familiar, Terraza, etc.)
**Datos de ejemplo**:
```sql
INSERT INTO TipoMesa (nombre, descripcion) VALUES 
('VIP', 'Mesas premium con servicio especial'),
('Familiar', 'Mesas para familias grandes'),
('Terraza', 'Mesas al aire libre'),
('Estándar', 'Mesas regulares del restaurante');
```

### 3. Tabla `Mesa`
```sql
CREATE TABLE Mesa (
    idMesa INT AUTO_INCREMENT PRIMARY KEY,
    idTipoMesa INT NOT NULL,
    capacidad INT NOT NULL,
    FOREIGN KEY (idTipoMesa) REFERENCES TipoMesa(idTipoMesa)
);
```

**Propósito**: Representa mesas físicas individuales
**Nota**: Actualmente no se usa directamente en las reservas

### 4. Tabla `ConfiguracionFranja`
```sql
CREATE TABLE ConfiguracionFranja (
    idFranja INT AUTO_INCREMENT PRIMARY KEY,
    franjaHoraria VARCHAR(100) NOT NULL,
    capacidadMaxima INT NOT NULL,
    cantidadMesas INT NOT NULL
);
```

**Propósito**: Define franjas horarias y sus límites de capacidad
**Datos de ejemplo**:
```sql
INSERT INTO ConfiguracionFranja (franjaHoraria, capacidadMaxima, cantidadMesas) VALUES 
('10:00 - 12:00', 80, 20),
('12:30 - 14:30', 100, 25),
('15:00 - 17:00', 60, 15),
('18:00 - 20:00', 120, 30),
('20:30 - 22:30', 100, 25);
```

### 5. Tabla `Reserva` (Principal)
```sql
CREATE TABLE Reserva (
    idReserva INT AUTO_INCREMENT PRIMARY KEY,
    nombreCliente VARCHAR(255) NOT NULL,
    correoCliente VARCHAR(255) NOT NULL,
    telefonoCliente VARCHAR(20),
    fecha DATE NOT NULL,
    numeroPersonas INT NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'CONFIRMADA',
    comentarios TEXT,
    idFranja INT NOT NULL,
    idUsuario INT,
    idTipoMesa INT NOT NULL,
    FOREIGN KEY (idFranja) REFERENCES ConfiguracionFranja(idFranja),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idTipoMesa) REFERENCES TipoMesa(idTipoMesa)
);
```

**Propósito**: Almacena todas las reservas del sistema
**Estados válidos**: 'CONFIRMADA', 'CANCELADA', 'COMPLETADA'

---

## 🔍 Consultas Principales del Sistema

### 1. Consultas de Disponibilidad

#### Personas ocupadas por franja:
```sql
SELECT SUM(r.numeroPersonas) 
FROM Reserva r 
WHERE r.fecha = ? 
  AND r.franja.idFranja = ? 
  AND r.estado = 'CONFIRMADA';
```

#### Mesas ocupadas por franja:
```sql
SELECT SUM(CASE WHEN r.numeroPersonas <= 5 THEN 1 ELSE 2 END) 
FROM Reserva r 
WHERE r.fecha = ? 
  AND r.franja.idFranja = ? 
  AND r.estado = 'CONFIRMADA';
```

#### Mesas ocupadas por tipo específico:
```sql
SELECT SUM(CASE WHEN r.numeroPersonas <= 5 THEN 1 ELSE 2 END) 
FROM Reserva r 
WHERE r.fecha = ? 
  AND r.franja.idFranja = ? 
  AND r.tipoMesa.idTipoMesa = ? 
  AND r.estado = 'CONFIRMADA';
```

### 2. Consultas de Validación

#### Verificar reserva existente del usuario:
```sql
SELECT * FROM Reserva 
WHERE idUsuario = ? 
  AND fecha = ? 
  AND estado = 'CONFIRMADA';
```

#### Autenticación de usuario:
```sql
SELECT * FROM Usuarios 
WHERE usuario = ?;
```

### 3. Consultas Administrativas

#### Reservas por fecha:
```sql
SELECT r.*, u.nombreCompleto as usuarioNombre, 
       tm.nombre as tipoMesaNombre, 
       cf.franjaHoraria
FROM Reserva r
LEFT JOIN Usuarios u ON r.idUsuario = u.idUsuario
JOIN TipoMesa tm ON r.idTipoMesa = tm.idTipoMesa
JOIN ConfiguracionFranja cf ON r.idFranja = cf.idFranja
WHERE r.fecha = ?
ORDER BY cf.idFranja, r.idReserva;
```

---

## 📈 Optimización y Performance

### 1. Índices Recomendados

```sql
-- Índice compuesto para consultas de disponibilidad
CREATE INDEX idx_reserva_fecha_franja_estado 
ON Reserva(fecha, idFranja, estado);

-- Índice para consultas por usuario
CREATE INDEX idx_reserva_usuario_fecha 
ON Reserva(idUsuario, fecha);

-- Índice para consultas por tipo de mesa
CREATE INDEX idx_reserva_tipo_mesa 
ON Reserva(idTipoMesa, fecha, idFranja);
```

### 2. Estrategias de Optimización

#### Connection Pooling:
```properties
# HikariCP configuration
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

#### Query Optimization:
- Uso de `@Query` para consultas específicas
- Consultas nativas cuando es necesario
- Lazy loading para relaciones no esenciales

---

## 🔄 Gestión de Transacciones

### 1. Niveles de Aislamiento

Spring Boot configura automáticamente el nivel de aislamiento, pero se puede personalizar:

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void crearReserva(...) {
    // Operaciones transaccionales
}
```

### 2. Propagación de Transacciones

```java
// Nueva transacción
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void operacionIndependiente() { ... }

// Solo lectura (optimización)
@Transactional(readOnly = true)
public List<Reserva> consultarReservas() { ... }
```

### 3. Rollback Automático

```java
@Transactional
public Reserva crearReserva(ReservaFormDTO formDTO, String username) {
    try {
        // Operaciones múltiples
        validarDisponibilidad();
        crearEntidadReserva();
        guardarReserva();
        
        return reserva;
    } catch (Exception e) {
        // Rollback automático en caso de RuntimeException
        throw new IllegalStateException("Error al crear reserva", e);
    }
}
```

---

## 📊 Monitoreo y Logging

### 1. Logging de SQL

```properties
# Mostrar SQL generado
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging de parámetros
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 2. Consola H2 para Desarrollo

Acceso: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:file:./sietesopasdb`
- **Username**: `sa`
- **Password**: (vacío)

### 3. Métricas de Performance

```java
// En el Service, se pueden agregar métricas
@Timed(name = "reserva.creation.time", description = "Time taken to create a reservation")
public Reserva crearReserva(...) {
    // Lógica de creación
}
```

---

## 🛡️ Seguridad de Datos

### 1. Encriptación de Contraseñas

```java
@Bean
public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// En el servicio
usuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena()));
```

### 2. Validación de Integridad

```java
// Validaciones JPA automáticas
@Column(name = "correo", unique = true, nullable = false)
private String correo;

@ManyToOne
@JoinColumn(name = "idFranja", nullable = false)
private ConfiguracionFranja franja;
```

### 3. Prevención de SQL Injection

Spring Data JPA usa **prepared statements** automáticamente:

```java
// Seguro - usa parámetros preparados
@Query("SELECT r FROM Reserva r WHERE r.fecha = :fecha AND r.estado = :estado")
List<Reserva> findByFechaAndEstado(@Param("fecha") LocalDate fecha, 
                                   @Param("estado") String estado);
```

---

Esta documentación cubre todos los aspectos del manejo de base de datos en el sistema, desde la configuración hasta las consultas específicas y optimizaciones de performance.
