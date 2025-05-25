/**
 * Clase DAO para acceder a los datos de la tabla Tratamiento.
 * Permite consultar los tratamientos realizados a las mascotas.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

import java.sql.*;
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
}

