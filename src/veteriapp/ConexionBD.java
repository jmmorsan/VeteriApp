/**
 * Clase que gestiona la conexión a la base de datos MySQL.
 * Utiliza atributos separados para máquina, base de datos, usuario y contraseña.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Declaración de constantes para la conexión

    private static final String MAQUINA = "localhost";
    private static final String BD = "CreacionBD";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "1234";

    /**
     * Método que establece y devuelve la conexión con la base de datos
     * 
     * @return objeto Connection si tiene éxito, o null si hay error
     */
    public static Connection getConexion() {
        Connection conexion = null;
        String url = "jdbc:mysql://" + MAQUINA + "/" + BD;

        try {
            conexion = DriverManager.getConnection(url, USUARIO, PASSWORD);
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }

        return conexion;
    }
}
