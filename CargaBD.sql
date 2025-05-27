USE VeteriApp;

-- Insertar dueños
INSERT INTO Dueno (dniDueno, nombre, apellidos, telefono, email, direccion) VALUES
('45811714S', 'Juan Manuel', 'Moreno Sánchez', '618947013', 'jmmorenosanchez0903@gmail.com', 'Calle Juan Méndez Casado 17, Palomares del Río, Sevilla'),
('87654321B', 'Laura', 'Martínez López', '600112233', 'laura.martinez@example.com', 'Calle Luna 12, Sevilla'),
('11223344C', 'Carlos', 'Gómez Ruiz', '611223344', 'carlos.gomez@example.com', 'Avda. Sol 34, Valencia'),
('22334455D', 'Ana', 'Ruiz García', '622334455', 'ana.ruiz@example.com', 'Calle Naranjo 5, Dos Hermanas, Sevilla'),
('33445566E', 'Pedro', 'Sánchez Días', '633445566', 'pedro.sanchez@example.com', 'Plaza Mayor 8, Mairena del Aljarafe, Sevilla');


-- Insertar mascotas
INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, peso, estado, fechaFallecimiento, notasMemorial, dniDueno) VALUES
('Canelo', 'Perro', 'Mestizo', '2013-03-09', 3.7, 'fallecida', '2024-12-23', 'Mascota muy querida y amada por todo el que lo conocia, Canelo te alegraba el dia incluso en sus peores momentos, era una mascota unica.', '45811714S'),
('Nina', 'Perro', 'Golden Retriever', '2018-05-10', 28.5, 'activa', NULL, NULL, '87654321B'),
('Toby', 'Gato', 'Siames', '2017-09-15', 5.1, 'fallecida', '2023-06-01', 'Falleció debido a complicaciones renales. Muy echado de menos por su familia.', '11223344C'),
('Luna', 'Gato', 'Persa', '2020-01-20', 4.2, 'activa', NULL, NULL, '22334455D'),
('Max', 'Perro', 'Pastor Alemán', '2019-07-01', 35.0, 'activa', NULL, NULL, '33445566E'),
('Kitty', 'Gato', 'Común Europeo', '2021-11-11', 3.5, 'activa', NULL, NULL, '87654321B'),
('Rocky', 'Perro', 'Labrador', '2017-02-28', 30.1, 'activa', NULL, NULL, '11223344C');


-- Insertar veterinarios
INSERT INTO Veterinario (dniVeterinario, nombre, apellidos, especialidad, telefono, email) VALUES
('98765432D', 'Lucía', 'Rodríguez Pérez', 'Medicina General', '600111222', 'lucia.rodiguez@veteriapp.com'),
('23456789E', 'David', 'García López', 'Cirugía Veterinaria', '600333444', 'david.garcia@veteriapp.com'),
('34567890F', 'María', 'Pérez', 'Cirugía', '620334455', 'maria.perez@veteriapp.com'),
('45678901G', 'Juan', 'Fernández', 'Medicina General', '630445566', 'juan.fernandez@veteriapp.com'),
('56789012H', 'Elena', 'Ruiz Martín', 'Dermatología', '644556677', 'elena.ruiz@veteriapp.com'),
('67890123I', 'Sergio', 'López Castro', 'Oftalmología', '655667788', 'sergio.lopez@veteriapp.com');


-- Insertar citas
INSERT INTO Cita (fechaHora, motivo, estado, idMascota) VALUES
('2022-04-15 11:00:00', 'Revisión general anual', 'realizada', 1),
('2023-03-10 10:30:00', 'Vacunación anual + desparasitación', 'realizada', 1),
('2024-12-20 09:00:00', 'Revisión urgente por síntomas graves', 'realizada', 1),
('2024-04-20 10:00:00', 'Revisión anual', 'realizada', 2),
('2024-04-25 12:30:00', 'Vacuna antirrábica', 'pendiente', 2),
('2025-06-01 10:00:00', 'Chequeo de rutina', 'pendiente', 4),
('2025-06-05 15:00:00', 'Consulta por problema de piel', 'pendiente', 5),
('2024-11-15 16:00:00', 'Revisión dental', 'realizada', 7);


-- Relación cita ↔ veterinarios
INSERT INTO VeterinarioCita (idCita, dniVeterinario) VALUES
(1, '98765432D'),
(2, '98765432D'),
(3, '98765432D'),
(3, '23456789E'),
(4, '34567890F'),
(5, '45678901G'),
(6, '98765432D'),
(7, '56789012H'),
(8, '23456789E');


-- Insertar tratamientos
INSERT INTO Tratamiento (tipo, descripcion, fecha, observaciones, idMascota) VALUES
('Revisión', 'Chequeo completo y análisis de sangre', '2022-04-15', 'Buen estado general, sin anomalías', 1),
('Vacuna', 'Vacuna anual polivalente y desparasitación interna', '2023-03-10', 'Vacuna administrada sin complicaciones', 1),
('Control', 'Exploración por debilidad y pérdida de apetito', '2024-12-20', 'Se detecta fallo multiorgánico. Seguimiento intensivo.', 1),
('Vacuna', 'Vacuna antirrábica anual', '2024-04-25', 'Pendiente de aplicar', 2),
('Revisión', 'Chequeo completo', '2024-04-20', 'Todo en orden', 2),
('Desparasitación', 'Desparasitación interna con pastilla', '2025-05-20', 'Procedimiento estándar', 4),
('Análisis Piel', 'Toma de muestras y análisis de piel para posible alergia', '2025-06-05', 'Resultados pendientes', 5),
('Extracción Dental', 'Extracción de molar afectado', '2024-11-15', 'Extracción exitosa, se recupera bien', 7);


-- Relación tratamiento ↔ veterinarios
INSERT INTO VeterinarioTratamiento (idTratamiento, dniVeterinario) VALUES
(1, '98765432D'),
(2, '98765432D'),
(3, '98765432D'),
(3, '23456789E'),
(4, '45678901G'),
(5, '34567890F'),
(6, '98765432D'),
(7, '56789012H'),
(8, '23456789E');