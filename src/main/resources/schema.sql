-- Tabla de Usuarios
CREATE TABLE Usuarios (
  idUsuario INT AUTO_INCREMENT PRIMARY KEY,
  nombreCompleto VARCHAR(255) NOT NULL,
  correo VARCHAR(255) UNIQUE NOT NULL,
  telefono VARCHAR(50),
  usuario VARCHAR(50),
  contrasena VARCHAR(255) NOT NULL, -- usa 'contrasena' en vez de 'contraseña'
  rol VARCHAR(50) NOT NULL          -- en vez de ENUM
);

-- Tabla de Tipos de Mesas
CREATE TABLE TipoMesa (
    idTipoMesa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

-- Tabla de Mesas
CREATE TABLE Mesa (
    idMesa INT AUTO_INCREMENT PRIMARY KEY,
    idTipoMesa INT NOT NULL,
    capacidad INT NOT NULL,
    FOREIGN KEY (idTipoMesa) REFERENCES TipoMesa(idTipoMesa)
);

-- Tabla de Configuración de Franjas Horarias
CREATE TABLE ConfiguracionFranja (
    idFranja INT AUTO_INCREMENT PRIMARY KEY,
    franjaHoraria VARCHAR(100) NOT NULL,
    capacidadMaxima INT NOT NULL,
    cantidadMesas INT NOT NULL
);

-- Tabla de Mesas por Franja
CREATE TABLE MesaPorFranja (
    idMesaPorFranja INT AUTO_INCREMENT PRIMARY KEY,
    idMesa INT NOT NULL,
    idFranja INT NOT NULL,
    fecha DATE NOT NULL,
    disponible BOOLEAN NOT NULL,
    FOREIGN KEY (idMesa) REFERENCES Mesa(idMesa),
    FOREIGN KEY (idFranja) REFERENCES ConfiguracionFranja(idFranja)
);

-- Tabla de Reservas
CREATE TABLE Reserva (
    idReserva INT AUTO_INCREMENT PRIMARY KEY,
    nombreCliente VARCHAR(255) NOT NULL,
    correoCliente VARCHAR(255) NOT NULL,
    telefonoCliente VARCHAR(50),
    fecha DATE NOT NULL,
    numeroPersonas INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    comentarios TEXT,
    idFranja INT NOT NULL,
    idMesa INT NOT NULL,
    idUsuario INT,
    FOREIGN KEY (idFranja) REFERENCES ConfiguracionFranja(idFranja),
    FOREIGN KEY (idMesa) REFERENCES Mesa(idMesa),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario)
);

-- Tabla de Historial de Reservas
CREATE TABLE HistorialReserva (
    idHistorial INT AUTO_INCREMENT PRIMARY KEY,
    idReserva INT NOT NULL,
    idUsuario INT,
    fechaHora TIMESTAMP NOT NULL,
    FOREIGN KEY (idReserva) REFERENCES Reserva(idReserva),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario)
);
