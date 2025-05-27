USE VeteriApp;

-- Consultas

-- Consulta 1: Mascotas Activas sin Vacuna en el Último Año
SELECT M.nombre AS NombreMascota, M.especie, M.raza, D.nombre AS NombreDueno, D.apellidos AS ApellidosDueno 
FROM Mascota M 
JOIN Dueno D ON M.dniDueno = D.dniDueno 
WHERE M.estado = 'activa' AND M.idMascota NOT IN (SELECT DISTINCT T.idMascota FROM Tratamiento T WHERE T.tipo = 'Vacuna' AND T.fecha >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR));

-- Consulta 2: Veterinarios y su Número de Citas Asignadas
SELECT V.nombre, V.apellidos, V.especialidad, COUNT(VC.idCita) AS TotalCitasAsignadas 
FROM Veterinario V 
LEFT JOIN VeterinarioCita VC ON V.dniVeterinario = VC.dniVeterinario 
GROUP BY V.dniVeterinario, V.nombre, V.apellidos, V.especialidad 
ORDER BY TotalCitasAsignadas DESC;

-- Consulta 3: Número de Citas por Especie de Mascota
SELECT M.especie, COUNT(C.idCita) AS NumeroCitas 
FROM Cita C 
JOIN Mascota M ON C.idMascota = M.idMascota 
GROUP BY M.especie 
ORDER BY NumeroCitas DESC;

-- Consulta 4: Historial Completo de Citas y Tratamientos para una Mascota Fallecida Específica
-- Para una Mascota Fallecida de ID 1 (ejemplo)
SELECT 'Cita' AS TipoRegistro, C.fechaHora AS Fecha, C.motivo AS Descripcion, C.estado AS Estado, GROUP_CONCAT(DISTINCT V.nombre SEPARATOR ', ') AS VeterinariosInvolucrados 
FROM Cita C 
JOIN Mascota M ON C.idMascota = M.idMascota 
LEFT JOIN VeterinarioCita VC ON C.idCita = VC.idCita 
LEFT JOIN Veterinario V ON VC.dniVeterinario = V.dniVeterinario 
WHERE M.idMascota = 1 AND M.estado = 'fallecida' 
GROUP BY C.idCita
	UNION ALL 
		SELECT 'Tratamiento' AS TipoRegistro, T.fecha AS Fecha, T.descripcion AS Descripcion, T.tipo AS Estado, GROUP_CONCAT(DISTINCT V.nombre SEPARATOR ', ') AS VeterinariosInvolucrados 
		FROM Tratamiento T 
		JOIN Mascota M ON T.idMascota = M.idMascota 
		LEFT JOIN VeterinarioTratamiento VT ON T.idTratamiento = VT.idTratamiento 
		LEFT JOIN Veterinario V ON VT.dniVeterinario = V.dniVeterinario 
		WHERE M.idMascota = 1 AND M.estado = 'fallecida' 
		GROUP BY T.idTratamiento ORDER BY Fecha ASC;

-- Consulta 5: Dueños con Múltiples Mascotas y sus especies
SELECT D.dniDueno, D.nombre AS NombreDueno, D.apellidos AS ApellidosDueno, COUNT(M.idMascota) AS TotalMascotas, GROUP_CONCAT(M.especie SEPARATOR ', ') AS EspeciesMascotas 
FROM Dueno D 
JOIN Mascota M ON D.dniDueno = M.dniDueno 
GROUP BY D.dniDueno, D.nombre, D.apellidos 
HAVING COUNT(M.idMascota) > 1 
ORDER BY TotalMascotas DESC;

-- Vistas

-- Vista 1: Mascotas Fallecidas
CREATE OR REPLACE VIEW Vista_MascotasFallecidas 
AS SELECT M.idMascota, M.nombre, M.especie, M.fechaFallecimiento, D.nombre AS NombreDueno, D.apellidos AS ApellidosDueno 
FROM Mascota M 
JOIN Dueno D ON M.dniDueno = D.dniDueno 
WHERE M.estado = 'fallecida';

-- Vista 2: Especialidades Veterinarias con Citas Realizadas
CREATE OR REPLACE VIEW Vista_EspecialidadesConCitasRealizadas AS 
SELECT DISTINCT V.especialidad, COUNT(C.idCita) AS TotalCitasRealizadas 
FROM Veterinario V 
JOIN VeterinarioCita VC ON V.dniVeterinario = VC.dniVeterinario 
JOIN Cita C ON VC.idCita = C.idCita 
WHERE C.estado = 'realizada' GROUP BY V.especialidad;

-- Vista 3: Veterinarios que han asignado tratamientos de tipo 'Vacuna'
CREATE OR REPLACE VIEW Vista_VetsConTratamientosVacuna AS 
SELECT DISTINCT V.dniVeterinario, V.nombre, V.apellidos, V.especialidad 
FROM Veterinario V 
JOIN VeterinarioTratamiento VT ON V.dniVeterinario = VT.dniVeterinario 
JOIN Tratamiento T ON VT.idTratamiento = T.idTratamiento 
WHERE T.tipo = 'Vacuna';


-- Modificaciones

-- Modificación 1: Insertar nuevo Dueño y Mascota
INSERT INTO Dueno (dniDueno, nombre, apellidos, telefono, email, direccion) VALUES ('12345678Z', 'Laura', 'Gomez', '600111222', 'laura.gomez@example.com', 'Calle Falsa 123');
INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, peso, dniDueno) VALUES ('Boby', 'Perro', 'Labrador', '2022-01-15', 25.5, '12345678Z');

-- Modificación 2: Aumentar el peso de las mascotas de especie 'Gato' en un 10%
UPDATE Mascota 
SET peso = peso * 1.10 
WHERE especie = 'Gato';

-- Modificación 3: Cancelar citas futuras de mascotas cuyo dueño reside en 'Calle Desconocida'
UPDATE Cita C 
JOIN Mascota M ON C.idMascota = M.idMascota 
JOIN Dueno D ON M.dniDueno = D.dniDueno 
SET C.estado = 'cancelada' 
WHERE D.direccion = 'Calle Desconocida' AND C.fechaHora > NOW() AND C.estado = 'pendiente';

-- Modificación 4: Asignar un veterinario por defecto a citas sin veterinario asignado (ejemplo: reasignar citas de mascota ID 5)
UPDATE VeterinarioCita VC 
SET VC.dniVeterinario = '99999999Z' 
WHERE VC.idCita IN (SELECT idCita FROM Cita WHERE idMascota = 5);

-- Modificación 5: Reasignar todos los tratamientos y citas de un veterinario antiguo a uno nuevo
UPDATE VeterinarioTratamiento 
SET dniVeterinario = '77777777G'
WHERE dniVeterinario = '88888888H';

UPDATE VeterinarioCita 
SET dniVeterinario = '77777777G' 
WHERE dniVeterinario = '88888888H';

-- Modificación 6: Aumentar el peso de las mascotas que han recibido un tratamiento de tipo 'Cirugía' recientemente
UPDATE Mascota M 
JOIN Tratamiento T ON M.idMascota = T.idMascota 
SET M.peso = M.peso * 1.03 
WHERE T.tipo = 'Cirugía' AND T.fecha >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH);

-- Modificación 7: Eliminar citas que están 'cancelada' y son anteriores a cierta fecha
DELETE FROM Cita 
WHERE estado = 'cancelada' AND fechaHora < NOW();

-- Modificación 8: Eliminar veterinarios que no tienen ninguna cita ni tratamiento asignado
DELETE V 
FROM Veterinario V 
LEFT JOIN VeterinarioCita VC ON V.dniVeterinario = VC.dniVeterinario 
LEFT JOIN VeterinarioTratamiento VT ON V.dniVeterinario = VT.dniVeterinario 
WHERE VC.idCita IS NULL AND VT.idTratamiento IS NULL;

-- Modificación 9: Eliminar un Dueño y todas sus Mascotas (y su historial)

DELETE FROM Dueno 
WHERE dniDueno = '12345678Z';

-- Procedimientos Almacenados

-- Procedimiento Almacenado 1: RegistrarCitaConVeterinarios
DELIMITER //
CREATE PROCEDURE RegistrarCitaConVeterinarios(IN p_fechaHora DATETIME, IN p_motivo VARCHAR(255), IN p_idMascota INT, IN p_dniVeterinarios TEXT)
BEGIN
    DECLARE v_idCita INT;
    DECLARE v_dniVeterinario VARCHAR(9);
    DECLARE v_offset INT DEFAULT 0;
    DECLARE v_length INT DEFAULT 0;
    INSERT INTO Cita (fechaHora, motivo, estado, idMascota) VALUES (p_fechaHora, p_motivo, 'pendiente', p_idMascota);
    SET v_idCita = LAST_INSERT_ID();
    WHILE v_offset < LENGTH(p_dniVeterinarios) DO
        SET v_length = LOCATE(',', p_dniVeterinarios, v_offset + 1);
        IF v_length = 0 THEN
            SET v_length = LENGTH(p_dniVeterinarios) + 1;
        END IF;
        SET v_dniVeterinario = TRIM(SUBSTRING(p_dniVeterinarios, v_offset + 1, v_length - v_offset - 1));
        INSERT INTO VeterinarioCita (idCita, dniVeterinario) VALUES (v_idCita, v_dniVeterinario);
        SET v_offset = v_length;
    END WHILE;
END //
DELIMITER ;

-- Ejemplo de uso: CALL RegistrarCitaConVeterinarios('2025-06-10 15:00:00', 'Revisión general', 1, '12345678A,87654321B');

-- Procedimiento Almacenado 2: ObtenerTratamientosDeMascotaPorPeriodo
DELIMITER //
CREATE PROCEDURE ObtenerTratamientosDeMascotaPorPeriodo(IN p_idMascota INT, IN p_fechaInicio DATE, IN p_fechaFin DATE)
BEGIN
    SELECT T.idTratamiento, T.tipo, T.descripcion, T.fecha, T.observaciones, M.nombre AS NombreMascota, D.nombre AS NombreDueno, D.apellidos AS ApellidosDueno FROM Tratamiento T JOIN Mascota M ON T.idMascota = M.idMascota JOIN Dueno D ON M.dniDueno = D.dniDueno WHERE T.idMascota = p_idMascota AND T.fecha BETWEEN p_fechaInicio AND p_fechaFin ORDER BY T.fecha DESC;
END //
DELIMITER ;

-- Ejemplo de uso: CALL ObtenerTratamientosDeMascotaPorPeriodo(1, '2024-01-01', '2025-05-31');

-- Función 1: CalcularEdadMascota
DELIMITER //
CREATE FUNCTION CalcularEdadMascota(p_fechaNacimiento DATE) RETURNS INT DETERMINISTIC
BEGIN
    DECLARE v_edad INT;
    SET v_edad = TIMESTAMPDIFF(YEAR, p_fechaNacimiento, CURDATE());
    RETURN v_edad;
END //
DELIMITER ;

-- Ejemplo de uso: SELECT nombre, especie, CalcularEdadMascota(fechaNacimiento) AS Edad FROM Mascota;


-- Disparadores

-- Disparador 1 (BEFORE INSERT): trg_BeforeInsertCita_CheckDuplicado
DELIMITER //
CREATE TRIGGER trg_BeforeInsertCita_CheckDuplicado BEFORE INSERT ON Cita
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM Cita WHERE idMascota = NEW.idMascota AND DATE(NEW.fechaHora) = DATE(fechaHora)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Ya existe una cita para esta mascota en la misma fecha (día completo).';
    END IF;
END //
DELIMITER ;

-- Disparador 2 (AFTER UPDATE): trg_AfterUpdateMascota_EstadoFallecido
DELIMITER //
CREATE TRIGGER trg_AfterUpdateMascota_EstadoFallecido AFTER UPDATE ON Mascota
FOR EACH ROW
BEGIN
    IF OLD.estado = 'activa' AND NEW.estado = 'fallecida' THEN
        IF NEW.fechaFallecimiento IS NULL THEN
            UPDATE Mascota SET fechaFallecimiento = CURDATE() WHERE idMascota = NEW.idMascota;
        END IF;
        UPDATE Cita SET estado = 'cancelada' WHERE idMascota = NEW.idMascota AND fechaHora > NOW() AND estado = 'pendiente';
    END IF;
END //
DELIMITER ;