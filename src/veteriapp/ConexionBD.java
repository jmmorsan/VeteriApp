package veteriapp;

import java.sql.*;


/**
 * Clase de conexión a la base de datos.
 */
class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost/VeteriApp";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }
}
