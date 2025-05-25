/**
 * Clase DAO para acceder a los datos de la tabla Veterinario.
 * Gestiona operaciones CRUD sobre el personal veterinario de la clínica.
 * 
 * @author Juan Manuel
 * @version 2.0
 * @since 2025-05-25
 */

package veteriapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {

    // Declaración de variables
    private final Connection conexion;

    /**
     * Constructor que establece la conexión con la base de datos
     */
    public VeterinarioDAO() {
        conexion = ConexionBD.getConexion();
    }

    /**
     * Método para obtener todos los veterinarios registrados
     * 
     * @return Lista de objetos Veterinario
     */
    public List<Veterinario> obtenerTodosLosVeterinarios() {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Veterinario";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Veterinario v = new Veterinario(
                    rs.getString("dniVeterinario"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("especialidad"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
                lista.add(v);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener veterinarios: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método para insertar un nuevo veterinario en la base de datos
     * 
     * @param v Objeto Veterinario que se desea insertar
     */
    public void insertarVeterinario(Veterinario v) {
        String sql = "INSERT INTO Veterinario (dniVeterinario, nombre, apellidos, especialidad, telefono, email) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setString(1, v.getDniVeterinario());
        	sentencia.setString(2, v.getNombre());
        	sentencia.setString(3, v.getApellidos());
        	sentencia.setString(4, v.getEspecialidad());
        	sentencia.setString(5, v.getTelefono());
        	sentencia.setString(6, v.getEmail());
        	sentencia.executeUpdate();
            System.out.println("Veterinario insertado con éxito.");
        } catch (SQLException e) {
            System.out.println("Error insertando veterinario: " + e.getMessage());
        }
    }

    /**
     * Método para eliminar un veterinario por su DNI
     * 
     * @param dni DNI del veterinario a eliminar
     */
    public void eliminarVeterinario(String dni) {
        String sql = "DELETE FROM Veterinario WHERE dniVeterinario = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            int filas = stmt.executeUpdate();
            System.out.println(filas > 0 ? "Veterinario eliminado." : "⚠ No se encontró el veterinario.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar veterinario: " + e.getMessage());
        }
    }

    /**
     * Método para modificar los datos de un veterinario existente
     * 
     * @param v Objeto Veterinario con los nuevos datos
     * @return Número de filas afectadas
     */
    public int modificarVeterinario(Veterinario v) {
        String sql = """
            UPDATE Veterinario
            SET nombre = ?, apellidos = ?, especialidad = ?, telefono = ?, email = ?
            WHERE dniVeterinario = ?
        """;

        int filas = 0;

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setString(1, v.getNombre());
        	sentencia.setString(2, v.getApellidos());
        	sentencia.setString(3, v.getEspecialidad());
        	sentencia.setString(4, v.getTelefono());
        	sentencia.setString(5, v.getEmail());
        	sentencia.setString(6, v.getDniVeterinario());

            filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "Veterinario modificado." : "⚠ No se encontró el veterinario.");
        } catch (SQLException e) {
            System.out.println("Error al modificar veterinario: " + e.getMessage());
        }

        return filas;
    }
    
    /**
     * Nuevo método: Obtener veterinarios y su número de citas asignadas (Consulta 2 avanzada)
     * @return Lista de Strings con los datos de los veterinarios y el conteo de citas.
     */
    public List<String> obtenerVeterinariosConCitasAsignadas() {
        List<String> resultados = new ArrayList<>();
        String sql = """
            SELECT V.nombre, V.apellidos, V.especialidad, COUNT(VC.idCita) AS TotalCitasAsignadas
            FROM Veterinario V
            LEFT JOIN VeterinarioCita VC ON V.dniVeterinario = VC.dniVeterinario
            GROUP BY V.dniVeterinario, V.nombre, V.apellidos, V.especialidad
            ORDER BY TotalCitasAsignadas DESC;
        """;
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultados.add(
                    "Veterinario: " + rs.getString("nombre") + " " + rs.getString("apellidos") +
                    " (Especialidad: " + rs.getString("especialidad") + ")" +
                    " - Citas Asignadas: " + rs.getInt("TotalCitasAsignadas")
                );
            }
            if (resultados.isEmpty()) {
                resultados.add("No se encontraron veterinarios o no tienen citas asignadas.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener veterinarios con citas: " + e.getMessage());
        }
        return resultados;
    }
}
