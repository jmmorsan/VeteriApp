package veteriapp;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;


class MascotaDAO {
    Connection con = ConexionBD.getConexion();

    public List<Mascota> obtenerTodasLasMascotas() {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT * FROM Mascota";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Mascota(
                        rs.getInt("idMascota"),
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null,
                        rs.getDouble("peso"),
                        EstadoMascota.valueOf(rs.getString("estado").toUpperCase()),
                        rs.getDate("fechaFallecimiento") != null ? rs.getDate("fechaFallecimiento").toLocalDate() : null,
                        rs.getString("dniDueno")));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return lista;
    }

    public void insertarMascota(Mascota m) {
        String sql = "INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, peso, estado, fechaFallecimiento, dniDueno) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, m.getNombre());
            stmt.setString(2, m.getEspecie());
            stmt.setString(3, m.getRaza());
            stmt.setDate(4, m.getFechaNacimiento() != null ? Date.valueOf(m.getFechaNacimiento()) : null);
            stmt.setDouble(5, m.getPeso());
            stmt.setString(6, m.getEstado().name().toLowerCase());
            stmt.setDate(7, m.getFechaFallecimiento() != null ? Date.valueOf(m.getFechaFallecimiento()) : null);
            stmt.setString(8, m.getDniDueno());
            stmt.executeUpdate();
            System.out.println("Mascota insertada.");
        } catch (SQLException e) {
            System.out.println("Error insertando mascota: " + e.getMessage());
        }
    }

    public void eliminarMascota(int id) {
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM Mascota WHERE idMascota=?")) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            System.out.println(filas > 0 ? "Eliminado con éxito." : "No encontrado.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }
}