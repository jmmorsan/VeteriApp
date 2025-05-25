/**
 * Clase DAO para acceder a los datos de la tabla Tratamiento.
 * Gestiona operaciones CRUD sobre los tratamientos realizados a las mascotas.
 * 
 * @author Juan Manuel
 * @version 2.0
 * @since 2025-05-25
 */

package veteriapp;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TratamientoDAO {

    // Declaración de variables
    private final Connection conexion;

    /**
     * Constructor que establece la conexión con la base de datos
     */
    public TratamientoDAO() {
        conexion = ConexionBD.getConexion();
    }

    /**
     * Método para obtener todos los tratamientos registrados
     * 
     * @return Lista de objetos Tratamiento
     */
    public List<Tratamiento> obtenerTodos() {
        List<Tratamiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tratamiento";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tratamiento t = new Tratamiento(
                    rs.getInt("idTratamiento"),
                    rs.getString("tipo"),
                    rs.getString("descripcion"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getString("observaciones"),
                    rs.getInt("idMascota")
                );
                lista.add(t);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener tratamientos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método para insertar un tratamiento nuevo en la base de datos
     * 
     * @param t Objeto Tratamiento a insertar
     */
    public void insertarTratamiento(Tratamiento t) {
        String sql = "INSERT INTO Tratamiento (tipo, descripcion, fecha, observaciones, idMascota) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setString(1, t.getTipo());
        	sentencia.setString(2, t.getDescripcion());
        	sentencia.setDate(3, Date.valueOf(t.getFecha()));
        	sentencia.setString(4, t.getObservaciones());
        	sentencia.setInt(5, t.getIdMascota());
        	sentencia.executeUpdate();
            System.out.println("Tratamiento insertado con éxito.");
        } catch (SQLException e) {
            System.out.println("Error insertando tratamiento: " + e.getMessage());
        }
    }

    /**
     * Método para eliminar un tratamiento por su ID
     * 
     * @param id ID del tratamiento a eliminar
     */
    public void eliminarTratamiento(int id) {
        String sql = "DELETE FROM Tratamiento WHERE idTratamiento = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setInt(1, id);
            int filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "Tratamiento eliminado." : " No se encontró el tratamiento.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar tratamiento: " + e.getMessage());
        }
    }

    /**
     * Método para modificar un tratamiento existente
     * 
     * @param t Objeto Tratamiento con los datos nuevos
     * @return Número de filas afectadas
     */
    public int modificarTratamiento(Tratamiento t) {
        String sql = """
            UPDATE Tratamiento
            SET tipo = ?, descripcion = ?, fecha = ?, observaciones = ?, idMascota = ?
            WHERE idTratamiento = ?
        """;

        int filas = 0;

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
        	sentencia.setString(1, t.getTipo());
        	sentencia.setString(2, t.getDescripcion());
        	sentencia.setDate(3, Date.valueOf(t.getFecha()));
        	sentencia.setString(4, t.getObservaciones());
        	sentencia.setInt(5, t.getIdMascota());
        	sentencia.setInt(6, t.getIdTratamiento());

            filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "Tratamiento modificado." : "⚠ No se encontró el tratamiento.");
        } catch (SQLException e) {
            System.out.println("Error al modificar tratamiento: " + e.getMessage());
        }

        return filas;
    }
    
    /**
     * Nuevo método: Obtener tratamientos de una mascota por un período específico (usando procedimiento almacenado)
     * @param idMascota ID de la mascota
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de Strings con los tratamientos encontrados
     */
    public List<String> obtenerTratamientosDeMascotaPorPeriodo(int idMascota, LocalDate fechaInicio, LocalDate fechaFin) {
        List<String> resultados = new ArrayList<>();
        try (CallableStatement cstmt = conexion.prepareCall("{CALL ObtenerTratamientosDeMascotaPorPeriodo(?, ?, ?)}")) {
            cstmt.setInt(1, idMascota);
            cstmt.setDate(2, Date.valueOf(fechaInicio));
            cstmt.setDate(3, Date.valueOf(fechaFin));

            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(
                        "ID Tratamiento: " + rs.getInt("idTratamiento") +
                        ", Tipo: " + rs.getString("tipo") +
                        ", Descripción: " + rs.getString("descripcion") +
                        ", Fecha: " + rs.getDate("fecha").toLocalDate() +
                        ", Observaciones: " + rs.getString("observaciones") +
                        ", Mascota: " + rs.getString("NombreMascota") +
                        ", Dueño: " + rs.getString("NombreDueno") + " " + rs.getString("ApellidosDueno")
                    );
                }
                if (resultados.isEmpty()) {
                    resultados.add("No se encontraron tratamientos para la mascota " + idMascota + " en el período especificado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener tratamientos por período: " + e.getMessage());
        }
        return resultados;
    }
}
