package veteriapp;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.*;

class TratamientoDAOTest {

    private static Connection conexion;
    private static TratamientoDAO tratamientoDAO;

    @BeforeAll
    static void setUp() throws SQLException {
        String url = "jdbc:mysql://localhost/VeteriApp";
        conexion = DriverManager.getConnection(url, "root", "1234");
        conexion.setAutoCommit(false); // Desactivar auto-commit
        tratamientoDAO = new TratamientoDAO(conexion);
    }

    @Test
    void testInsertarTratamiento() {
        Tratamiento nuevo = new Tratamiento(0, "Curación", "Tratamiento de herida leve",
                LocalDate.of(2025, 5, 25), "Revisar en 3 días", 1);
        
        int antes = tratamientoDAO.obtenerTodos().size();
        tratamientoDAO.insertarTratamiento(nuevo);
        int despues = tratamientoDAO.obtenerTodos().size();

        assertEquals(antes + 1, despues, "Debe haber un tratamiento más en la base de datos.");
    }

    @Test
    void testEliminarTratamiento() {
        // Primero insertamos uno que sepamos que vamos a borrar
        Tratamiento paraEliminar = new Tratamiento(0, "Prueba", "Eliminar este", 
                LocalDate.of(2025, 5, 25), "Observación de prueba", 1);
        tratamientoDAO.insertarTratamiento(paraEliminar);

        // Recuperamos el ID del tratamiento recién insertado
        List<Tratamiento> lista = tratamientoDAO.obtenerTodos();
        int ultimoID = lista.get(lista.size() - 1).getIdTratamiento();

        int antes = lista.size();
        tratamientoDAO.eliminarTratamiento(ultimoID);
        int despues = tratamientoDAO.obtenerTodos().size();

        assertEquals(antes - 1, despues, "Debe haber un tratamiento menos tras la eliminación.");
    }

    @Test
    void testObtenerTodos() {
        List<Tratamiento> lista = tratamientoDAO.obtenerTodos();
        assertNotNull(lista, "La lista no debe ser nula.");
        assertTrue(lista.size() >= 5, "Debe haber al menos 5 tratamientos preexistentes en la BD.");
    }

    @Test
    void testObtenerTratamientosPorPeriodo() {
        int idMascota = 1;
        LocalDate inicio = LocalDate.of(2022, 1, 1);
        LocalDate fin = LocalDate.of(2023, 12, 31);

        List<String> resultados = tratamientoDAO.obtenerTratamientosDeMascotaPorPeriodo(idMascota, inicio, fin);

        assertFalse(resultados.isEmpty(), "Debe haber tratamientos dentro del periodo indicado.");
        assertTrue(resultados.get(0).contains("ID Tratamiento"), "La cadena debe contener datos formateados correctamente.");
    }

    @AfterEach
    void rollbackCambios() throws SQLException {
        conexion.rollback(); // Volver al estado original tras cada test
    }

    @AfterAll
    static void cerrarConexion() throws SQLException {
        conexion.close();
    }
}
