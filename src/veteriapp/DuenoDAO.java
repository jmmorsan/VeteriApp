/**
 * Clase DAO para acceder a los datos de la tabla Dueno.
 * Gestiona operaciones de lectura sobre los propietarios de mascotas.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
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
            System.out.println("Error al obtener dueños: " + e.getMessage());
        }

        return lista;
    }
}
