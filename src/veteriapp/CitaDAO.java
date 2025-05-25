/**
 * Clase DAO para acceder a los datos de la tabla Cita.
 * Permite consultar el historial de citas veterinarias registradas.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

import java.sql.*;
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
}
