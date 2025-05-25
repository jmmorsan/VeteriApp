/**
 * Aplicación VeteriApp. Gestión de clínica veterinaria desde consola.
 * Se encarga de mostrar el menú y ejecutar las operaciones mediante métodos auxiliares.
 * 
 * Requiere conexión a la base de datos MySQL.
 * Cumple con las buenas prácticas de modularidad y documentación Javadoc.
 *
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */
package veteriapp;

import java.util.List;
import java.util.Scanner;

public class VeteriApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MascotaDAO mascotaDAO = new MascotaDAO();
        DuenoDAO duenoDAO = new DuenoDAO();
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        CitaDAO citaDAO = new CitaDAO();
        TratamientoDAO tratamientoDAO = new TratamientoDAO();

        int opcion;
        do {
            System.out.println("\n--- Menú Principal VeteriApp ---");
            System.out.println("1. Listar todas las mascotas");
            System.out.println("2. Registrar nueva mascota");
            System.out.println("3. Eliminar mascota");
            System.out.println("4. Listar dueños");
            System.out.println("5. Listar veterinarios");
            System.out.println("6. Listar citas");
            System.out.println("7. Listar tratamientos");
            System.out.println("8. Salir");
            System.out.print("Elige una opción: ");

            while (!scanner.hasNextInt()) {
                System.out.println("Introduce un número válido.");
                scanner.next();
            }
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> listarMascotas(mascotaDAO);
                case 2 -> registrarMascota(scanner, mascotaDAO);
                case 3 -> eliminarMascota(scanner, mascotaDAO);
                case 4 -> listarDuenos(duenoDAO);
                case 5 -> listarVeterinarios(veterinarioDAO);
                case 6 -> listarCitas(citaDAO);
                case 7 -> listarTratamientos(tratamientoDAO);
                case 8 -> System.out.println("Saliendo de VeteriApp...");
                default -> System.out.println("Opción no válida.");
            }

        } while (opcion != 8);

        scanner.close();
    }

    /** Lista todas las mascotas */
    private static void listarMascotas(MascotaDAO mascotaDAO) {
        List<Mascota> lista = mascotaDAO.obtenerTodasLasMascotas();
        System.out.println("\n--- Listado de Mascotas ---");
        lista.forEach(System.out::println);
    }

    /** Registra una nueva mascota */
    private static void registrarMascota(Scanner scanner, MascotaDAO mascotaDAO) {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Especie: ");
        String especie = scanner.nextLine();
        System.out.print("Raza: ");
        String raza = scanner.nextLine();
        System.out.print("Peso: ");
        while (!scanner.hasNextDouble()) { System.out.println("Número no válido."); scanner.next(); }
        double peso = scanner.nextDouble(); scanner.nextLine();
        System.out.print("DNI del dueño: ");
        String dni = scanner.nextLine();

        Mascota nueva = new Mascota(0, nombre, especie, raza, null, peso, EstadoMascota.ACTIVA, null, dni);
        mascotaDAO.insertarMascota(nueva);
    }

    /** Elimina mascota por ID */
    private static void eliminarMascota(Scanner scanner, MascotaDAO mascotaDAO) {
        System.out.print("ID a eliminar: ");
        while (!scanner.hasNextInt()) { System.out.println("Número no válido."); scanner.next(); }
        int id = scanner.nextInt(); scanner.nextLine();
        mascotaDAO.eliminarMascota(id);
    }

    /** Lista todos los dueños */
    private static void listarDuenos(DuenoDAO duenoDAO) {
        List<Dueno> lista = duenoDAO.obtenerTodosLosDuenos();
        System.out.println("\n--- Listado de Dueños ---");
        lista.forEach(System.out::println);
    }

    /** Lista todos los veterinarios */
    private static void listarVeterinarios(VeterinarioDAO veterinarioDAO) {
        List<Veterinario> lista = veterinarioDAO.obtenerTodosLosVeterinarios();
        System.out.println("\n--- Listado de Veterinarios ---");
        lista.forEach(System.out::println);
    }

    /** Lista todas las citas */
    private static void listarCitas(CitaDAO citaDAO) {
        List<Cita> lista = citaDAO.obtenerTodasLasCitas();
        System.out.println("\n--- Listado de Citas ---");
        lista.forEach(System.out::println);
    }

    /** Lista todos los tratamientos */
    private static void listarTratamientos(TratamientoDAO tratamientoDAO) {
        List<Tratamiento> lista = tratamientoDAO.obtenerTodos();
        System.out.println("\n--- Listado de Tratamientos ---");
        lista.forEach(System.out::println);
    }
}
