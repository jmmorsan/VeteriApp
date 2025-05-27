-- Crear la base de datos
DROP DATABASE IF EXISTS VeteriApp;
CREATE DATABASE VeteriApp CHARACTER SET utf8mb4;
USE VeteriApp;

-- Tabla: Dueno
CREATE TABLE Dueno (
    dniDueno VARCHAR(9) PRIMARY KEY, 
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(80),
    telefono VARCHAR(15), 
    email VARCHAR(100) UNIQUE,
    direccion VARCHAR(255)
);

-- Tabla: Mascota
CREATE TABLE Mascota (
    idMascota INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    especie VARCHAR(30),
    raza VARCHAR(50),
    fechaNacimiento DATE,
    peso DECIMAL(5,2),
    estado ENUM('activa', 'fallecida') DEFAULT 'activa',
    fechaFallecimiento DATE,
    dniDueno VARCHAR(9),
    notasMemorial VARCHAR(300) NULL,
    FOREIGN KEY (dniDueno) REFERENCES Dueno(dniDueno) ON DELETE CASCADE,
    UNIQUE (nombre, fechaNacimiento, dniDueno)
);

-- Tabla: Veterinario
CREATE TABLE Veterinario (
    dniVeterinario VARCHAR(9) PRIMARY KEY, 
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(80),
    especialidad VARCHAR(50),
    telefono VARCHAR(15), 
    email VARCHAR(100) UNIQUE
);

-- Tabla: Cita
CREATE TABLE Cita (
    idCita INT AUTO_INCREMENT PRIMARY KEY,
    fechaHora DATETIME NOT NULL,
    motivo VARCHAR(255),
    estado ENUM('pendiente', 'realizada', 'cancelada') DEFAULT 'pendiente',
    idMascota INT,
    FOREIGN KEY (idMascota) REFERENCES Mascota(idMascota) ON DELETE CASCADE,
    UNIQUE (fechaHora, idMascota)
);

-- Tabla intermedia: VeterinarioCita (relación N:N)
CREATE TABLE VeterinarioCita (
    idCita INT,
    dniVeterinario VARCHAR(9), 
    PRIMARY KEY (idCita, dniVeterinario),
    FOREIGN KEY (idCita) REFERENCES Cita(idCita) ON DELETE CASCADE,
    FOREIGN KEY (dniVeterinario) REFERENCES Veterinario(dniVeterinario) ON DELETE CASCADE
);

-- Tabla: Tratamiento
CREATE TABLE Tratamiento (
    idTratamiento INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50), -- vacuna, revisión, cirugía, etc.
    descripcion TEXT,
    fecha DATE NOT NULL,
    observaciones TEXT,
    idMascota INT,
    FOREIGN KEY (idMascota) REFERENCES Mascota(idMascota) ON DELETE CASCADE,
    UNIQUE (fecha, tipo, idMascota)
);

-- Tabla intermedia: VeterinarioTratamiento (relación N:N)
CREATE TABLE VeterinarioTratamiento (
    idTratamiento INT,
    dniVeterinario VARCHAR(9), 
    PRIMARY KEY (idTratamiento, dniVeterinario),
    FOREIGN KEY (idTratamiento) REFERENCES Tratamiento(idTratamiento) ON DELETE CASCADE,
    FOREIGN KEY (dniVeterinario) REFERENCES Veterinario(dniVeterinario) ON DELETE CASCADE
);