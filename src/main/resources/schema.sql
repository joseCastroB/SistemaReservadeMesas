CREATE TABLE IF NOT EXISTS estudiante (
    id INT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    apellido VARCHAR(200) NOT NULL, 
    codigo VARCHAR(10) UNIQUE,
    fecha_nacimiento DATE NOT NULL
);