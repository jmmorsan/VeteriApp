/**
 * Clase DAO para acceder a los datos de la tabla Mascota.
 * Gestiona operaciones CRUD desde Java hacia MySQL.
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

public class MascotaDAO {

    // Declaración de variables
    private final Connection conexion;
    
    public MascotaDAO(Connection conexion) { // <-- CAMBIO AQUÍ: Constructor con Connection
        this.conexion = conexion;
    }


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
                // Recuperar la fecha de fallecimiento, puede ser null
                Date fechaFallecimientoSql = rs.getDate("fechaFallecimiento");
                LocalDate fechaFallecimiento = (fechaFallecimientoSql != null) ? fechaFallecimientoSql.toLocalDate() : null;

                // Recuperar notasMemorial (puede ser null)
                String notasMemorial = rs.getString("notasMemorial");

                Mascota mascota = new Mascota(
                    rs.getInt("idMascota"),
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getDate("fechaNacimiento").toLocalDate(),
                    rs.getDouble("peso"),
                    EstadoMascota.valueOf(rs.getString("estado").toUpperCase()), // Convertir String a Enum
                    fechaFallecimiento,
                    rs.getString("dniDueno"),
                    notasMemorial // Incluir el nuevo campo
                );
                lista.add(mascota);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener todas las mascotas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Método para obtener una mascota por su ID.
     *
     * @param idMascota El ID de la mascota a buscar.
     * @return El objeto Mascota si se encuentra, o null si no existe.
     */
    public Mascota obtenerMascotaPorId(int idMascota) {
        Mascota mascota = null;
        String sql = "SELECT * FROM Mascota WHERE idMascota = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMascota);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Recuperar la fecha de fallecimiento, puede ser null
                    Date fechaFallecimientoSql = rs.getDate("fechaFallecimiento");
                    LocalDate fechaFallecimiento = (fechaFallecimientoSql != null) ? fechaFallecimientoSql.toLocalDate() : null;

                    // Recuperar notasMemorial (puede ser null)
                    String notasMemorial = rs.getString("notasMemorial");

                    mascota = new Mascota(
                        rs.getInt("idMascota"),
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getDate("fechaNacimiento").toLocalDate(),
                        rs.getDouble("peso"),
                        EstadoMascota.valueOf(rs.getString("estado").toUpperCase()),
                        fechaFallecimiento,
                        rs.getString("dniDueno"),
                        notasMemorial // Incluir el nuevo campo
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener mascota por ID: " + e.getMessage());
        }
        return mascota;
    }


    /**
     * Método para insertar una nueva mascota en la base de datos
     *
     * @param mascota Objeto Mascota a insertar
     * @return Número de filas afectadas
     */
    public int insertarMascota(Mascota mascota) {
        String sql = """
            INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, peso, estado, fechaFallecimiento, dniDueno, notasMemorial)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        int filas = 0;

        try (PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, mascota.getNombre());
            sentencia.setString(2, mascota.getEspecie());
            sentencia.setString(3, mascota.getRaza());
            sentencia.setDate(4, Date.valueOf(mascota.getFechaNacimiento()));
            sentencia.setDouble(5, mascota.getPeso());
            sentencia.setString(6, mascota.getEstado().name().toLowerCase());
            // Para fechaFallecimiento y notasMemorial, pueden ser null
            sentencia.setDate(7, mascota.getFechaFallecimiento() != null ? Date.valueOf(mascota.getFechaFallecimiento()) : null);
            sentencia.setString(8, mascota.getDniDueno());
            sentencia.setString(9, mascota.getNotasMemorial()); // Nuevo campo

            filas = sentencia.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = sentencia.getGeneratedKeys()) {
                    if (rs.next()) {
                        mascota.setIdMascota(rs.getInt(1)); // Asignar el ID generado
                    }
                }
                System.out.println("✅ Mascota '" + mascota.getNombre() + "' añadida con ID " + mascota.getIdMascota() + ".");
            } else {
                System.out.println("⚠ No se pudo añadir la mascota.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al añadir mascota: " + e.getMessage());
        }
        return filas;
    }


    /**
     * Método para modificar una mascota existente
     *
     * @param mascota Objeto Mascota con los nuevos datos
     * @return Número de filas afectadas
     */
    public int modificarMascota(Mascota mascota) {
        String sql = """
            UPDATE Mascota
            SET nombre = ?, especie = ?, raza = ?, fechaNacimiento = ?, peso = ?, estado = ?, fechaFallecimiento = ?, dniDueno = ?, notasMemorial = ?
            WHERE idMascota = ?
        """;

        int filas = 0;

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, mascota.getNombre());
            sentencia.setString(2, mascota.getEspecie());
            sentencia.setString(3, mascota.getRaza());
            sentencia.setDate(4, mascota.getFechaNacimiento() != null ? Date.valueOf(mascota.getFechaNacimiento()) : null);
            sentencia.setDouble(5, mascota.getPeso());
            sentencia.setString(6, mascota.getEstado().name().toLowerCase());
            sentencia.setDate(7, mascota.getFechaFallecimiento() != null ? Date.valueOf(mascota.getFechaFallecimiento()) : null);
            sentencia.setString(8, mascota.getDniDueno());
            sentencia.setString(9, mascota.getNotasMemorial()); // Nuevo campo
            sentencia.setInt(10, mascota.getIdMascota());

            filas = sentencia.executeUpdate();
            System.out.println(filas > 0 ? "✅ Mascota modificada." : "⚠ No se encontró la mascota.");
        } catch (SQLException e) {
            System.out.println("❌ Error al modificar mascota: " + e.getMessage());
        }
        return filas;
    }


    /**
     * Método para eliminar una mascota por su ID
     *
     * @param id ID de la mascota a eliminar
     */
    public void eliminarMascota(int id) {
        String sql = "DELETE FROM Mascota WHERE idMascota = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            System.out.println(filas > 0 ? "✅ Mascota eliminada." : "⚠ No se encontró la mascota.");
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar mascota: " + e.getMessage());
        }
    }
}