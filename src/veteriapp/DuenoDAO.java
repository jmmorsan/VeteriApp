/**
 * Clase DAO para acceder a los datos de la tabla Dueno.
 * Gestiona operaciones CRUD sobre los propietarios de mascotas.
 *
 * @author Juan Manuel
 * @version 2.0
 * @since 2025-05-25
 */

package veteriapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuenoDAO {

    // Declaración de variables
    private final Connection conexion;

    /**
     * Constructor que establece la conexión con la base de datos
     */
    public DuenoDAO() {
        conexion = ConexionBD.getConexion();
    }

    /**
     * Método para obtener todos los dueños registrados
     *
     * @return Lista de objetos Dueno
     */
    public List<Dueno> obtenerTodosLosDuenos() {
        List<Dueno> lista = new ArrayList<>();
        String sql = "SELECT * FROM Dueno";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Dueno dueno = new Dueno(
                    rs.getString("dniDueno"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                );
                lista.add(dueno);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener todos los dueños: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Método para obtener un dueño por su DNI.
     *
     * @param dniDueno El DNI del dueño a buscar.
     * @return El objeto Dueno si se encuentra, o null si no existe.
     */
    public Dueno obtenerDuenoPorDni(String dniDueno) {
        Dueno dueno = null;
        String sql = "SELECT * FROM Dueno WHERE dniDueno = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dniDueno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dueno = new Dueno(
                        rs.getString("dniDueno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener dueño por DNI: " + e.getMessage());
        }
        return dueno;
    }

    /**
     * Método para insertar un nuevo dueño en la base de datos
     *
     * @param d Objeto Dueno a insertar
     * @return Número de filas afectadas
     */
    public int insertarDueno(Dueno d) {
        String sql = """
            INSERT INTO Dueno (dniDueno, nombre, apellidos, telefono, email, direccion)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        int filas = 0;

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, d.getDniDueno());
            sentencia.setString(2, d.getNombre());
            sentencia.setString(3, d.getApellidos());
            sentencia.setString(4, d.getTelefono());
            sentencia.setString(5, d.getEmail());
            sentencia.setString(6, d.getDireccion());

            filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "✅ Dueño añadido." : "⚠ No se pudo añadir el dueño.");
        } catch (SQLException e) {
            System.out.println("❌ Error al añadir dueño: " + e.getMessage());
        }
        return filas;
    }

    /**
     * Método para modificar los datos de un dueño
     *
     * @param d Objeto Dueno con los datos actualizados
     * @return Número de filas afectadas
     */
    public int modificarDueno(Dueno d) {
        String sql = """
            UPDATE Dueno
            SET nombre = ?, apellidos = ?, telefono = ?, email = ?, direccion = ?
            WHERE dniDueno = ?
        """;

        int filas = 0;

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, d.getNombre());
            sentencia.setString(2, d.getApellidos());
            sentencia.setString(3, d.getTelefono());
            sentencia.setString(4, d.getEmail());
            sentencia.setString(5, d.getDireccion());
            sentencia.setString(6, d.getDniDueno());

            filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "✅ Dueño modificado." : "⚠ No se encontró el dueño.");
        } catch (SQLException e) {
            System.out.println("❌ Error al modificar dueño: " + e.getMessage());
        }
        return filas;
    }

    /**
     * Método para eliminar un dueño por su DNI
     *
     * @param dni DNI del dueño a eliminar
     */
    public void eliminarDueno(String dni) {
        String sql = "DELETE FROM Dueno WHERE dniDueno = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            int filas = stmt.executeUpdate();
            System.out.println(filas > 0 ? "✅ Dueño eliminado." : "⚠ No se encontró el dueño.");
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar dueño: " + e.getMessage());
        }
    }
}