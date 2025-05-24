package veteriapp;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

class VeterinarioDAO {
    Connection con = ConexionBD.getConexion();

    public List<Veterinario> obtenerTodosLosVeterinarios() {
        List<Veterinario> lista = new ArrayList<>();
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Veterinario")) {
            while (rs.next()) {
                lista.add(new Veterinario(
                        rs.getString("dniVeterinario"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("especialidad"),
                        rs.getString("telefono"),
                        rs.getString("email")));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return lista;
    }
}