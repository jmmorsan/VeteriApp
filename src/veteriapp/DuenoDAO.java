package veteriapp;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

class DuenoDAO {
    Connection con = ConexionBD.getConexion();

    public List<Dueno> obtenerTodosLosDuenos() {
        List<Dueno> lista = new ArrayList<>();
        String sql = "SELECT * FROM Dueno";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Dueno(
                        rs.getString("dniDueno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion")));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return lista;
    }
}