/**
 * Clase DAO para acceder a los datos de la tabla Veterinario.
 * Permite consultar la lista de veterinarios de la clínica.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
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
}
