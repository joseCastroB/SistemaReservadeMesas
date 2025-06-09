INSERT INTO TipoMesa (nombre, descripcion) VALUES
('Mesa Común', 'Mesa en el salón principal para 5 personas máximo'),
('Mesa en terraza', 'Mesa en la terraza para disfrutar vista para 5 personas máximo'),
('Mesa privada', 'Mesa privada para negocios o privacidad para 5 personas máximo'),
('Mesa cerca a la ventana', 'Mesa cerca a la ventana para visibilidad de la calle para 5 personas máximo');

INSERT INTO ConfiguracionFranja (franjaHoraria, capacidadMaxima, cantidadMesas) VALUES
('10:00 - 12:00', 100, 20),
('12:00 - 14:00', 100, 20),
('14:00 - 16:00', 100, 20),
('16:00 - 18:00', 100, 20),
('18:00 - 20:00', 100, 20),
('20:00 - 22:00', 100, 20),
('22:00 - 24:00', 100, 20);

INSERT INTO Usuarios (nombreCompleto, correo, telefono, usuario, contraseña, rol)
VALUES ('Juan Pérez', 'admin@sietesopas.com', '987654321', 'admin', 'admin123', 'ADMINISTRADOR');