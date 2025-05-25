/**
 * Clase DAO para acceder a los datos de la tabla Mascota.
 * Gestiona operaciones CRUD desde Java hacia MySQL.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAO {
	
	// Declaración de variables
    private final Connection conexion;
    
    /**
     * Constructor que establece la conexión con la base de datos
     */
    public MascotaDAO() {
        conexion = ConexionBD.getConexion();
    }

    /**
     * Método para obtener todas las mascotas registradas en la base de datos
     * 
     * @return Lista con objetos de tipo Mascota
     */
    public List<Mascota> obtenerTodasLasMascotas() {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT * FROM Mascota";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Mascota mascota = new Mascota(
                    rs.getInt("idMascota"),
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null,
                    rs.getDouble("peso"),
                    EstadoMascota.valueOf(rs.getString("estado").toUpperCase()),
                    rs.getDate("fechaFallecimiento") != null ? rs.getDate("fechaFallecimiento").toLocalDate() : null,
                    rs.getString("dniDueno")
                );
                lista.add(mascota);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener mascotas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Método para insertar una nueva mascota en la base de datos
     * 
     * @param m Objeto de tipo Mascota que se desea insertar
     */
    public void insertarMascota(Mascota m) {
        String sql = "INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, peso, estado, fechaFallecimiento, dniDueno) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, m.getNombre());
            stmt.setString(2, m.getEspecie());
            stmt.setString(3, m.getRaza());
            stmt.setDate(4, m.getFechaNacimiento() != null ? Date.valueOf(m.getFechaNacimiento()) : null);
            stmt.setDouble(5, m.getPeso());
            stmt.setString(6, m.getEstado().name().toLowerCase());
            stmt.setDate(7, m.getFechaFallecimiento() != null ? Date.valueOf(m.getFechaFallecimiento()) : null);
            stmt.setString(8, m.getDniDueno());
            stmt.executeUpdate();
            System.out.println("✅ Mascota insertada con éxito.");
        } catch (SQLException e) {
            System.out.println("❌ Error insertando mascota: " + e.getMessage());
        }
    }

    /**
     * Método para eliminar una mascota a partir de su ID
     * 
     * @param idMascota ID de la mascota que se desea eliminar
     */
    public void eliminarMascota(int idMascota) {
        String sql = "DELETE FROM Mascota WHERE idMascota = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMascota);
            int filas = stmt.executeUpdate();
            System.out.println(filas > 0 ? "✅ Mascota eliminada." : "⚠ No se encontró la mascota.");
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar mascota: " + e.getMessage());
        }
    }
}
