/**
 * Clase DAO para acceder a los datos de la tabla Cita.
 * Gestiona operaciones CRUD sobre las citas veterinarias registradas.
 * 
 * @author Juan Manuel
 * @version 2.0
 * @since 2025-05-25
 */

package veteriapp;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    // Declaración de variables
    private final Connection conexion;

    /**
     * Constructor que establece la conexión con la base de datos
     */
    public CitaDAO() {
        conexion = ConexionBD.getConexion();
    }

    /**
     * Método para obtener todas las citas registradas
     * 
     * @return Lista de objetos Cita
     */
    public List<Cita> obtenerTodasLasCitas() {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cita";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cita cita = new Cita(
                    rs.getInt("idCita"),
                    rs.getTimestamp("fechaHora").toLocalDateTime(),
                    rs.getString("motivo"),
                    EstadoCita.valueOf(rs.getString("estado").toUpperCase()),
                    rs.getInt("idMascota")
                );
                lista.add(cita);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener citas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método para insertar una nueva cita veterinaria
     * 
     * @param c Objeto Cita que se desea insertar
     */
    public void insertarCita(Cita c) {
        String sql = "INSERT INTO Cita (fechaHora, motivo, estado, idMascota) VALUES (?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setTimestamp(1, Timestamp.valueOf(c.getFechaHora()));
        	sentencia.setString(2, c.getMotivo());
        	sentencia.setString(3, c.getEstado().name().toLowerCase());
        	sentencia.setInt(4, c.getIdMascota());
        	sentencia.executeUpdate();
            System.out.println("Cita insertada con éxito.");
        } catch (SQLException e) {
            System.out.println("Error insertando cita: " + e.getMessage());
        }
    }
    
    /**
     * Nuevo método: Registrar una cita y asignarle múltiples veterinarios
     * @param fechaHora Fecha y hora de la cita
     * @param motivo Motivo de la cita
     * @param idMascota ID de la mascota
     * @param dniVeterinarios Lista de DNIs de los veterinarios (separados por coma)
     */
    public void registrarCitaConVeterinarios(LocalDateTime fechaHora, String motivo, int idMascota, String dniVeterinarios) {
        try (CallableStatement cstmt = conexion.prepareCall("{CALL RegistrarCitaConVeterinarios(?, ?, ?, ?)}")) {
            cstmt.setTimestamp(1, Timestamp.valueOf(fechaHora));
            cstmt.setString(2, motivo);
            cstmt.setInt(3, idMascota);
            cstmt.setString(4, dniVeterinarios);
            cstmt.execute();
            System.out.println("Cita registrada y veterinarios asignados mediante procedimiento almacenado.");
        } catch (SQLException e) {
            System.out.println("Error al registrar cita con veterinarios: " + e.getMessage());
        }
    }

    /**
     * Método para eliminar una cita por su ID
     * 
     * @param id ID de la cita a eliminar
     */
    public void eliminarCita(int id) {
        String sql = "DELETE FROM Cita WHERE idCita = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setInt(1, id);
            int filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "✅ Cita eliminada." : "⚠ No se encontró la cita.");
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar cita: " + e.getMessage());
        }
    }

    /**
     * Método para modificar una cita existente
     * 
     * @param c Objeto Cita con los nuevos datos
     * @return Número de filas afectadas
     */
    public int modificarCita(Cita c) {
        String sql = """
            UPDATE Cita
            SET fechaHora = ?, motivo = ?, estado = ?, idMascota = ?
            WHERE idCita = ?
        """;

        int filas = 0;

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setTimestamp(1, Timestamp.valueOf(c.getFechaHora()));
        	sentencia.setString(2, c.getMotivo());
        	sentencia.setString(3, c.getEstado().name().toLowerCase());
        	sentencia.setInt(4, c.getIdMascota());
        	sentencia.setInt(5, c.getIdCita());

            filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "Cita modificada." : " No se encontró la cita.");
        } catch (SQLException e) {
            System.out.println("Error al modificar cita: " + e.getMessage());
        }

        return filas;
    }
}
