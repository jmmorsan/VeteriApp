/**
 * Aplicación VeteriApp. Gestión de clínica veterinaria desde consola.
 * Se encarga de mostrar el menú principal y ejecutar las operaciones CRUD
 * para las diferentes entidades (mascotas, dueños, veterinarios, citas y tratamientos)
 * mediante la invocación de métodos auxiliares que interactúan con las clases DAO.
 *
 * Esta clase actúa como la interfaz de usuario principal para la gestión de la clínica.
 *
 * @author Juan Manuel
 * @version 2.0
 * @since 2025-05-25
 */
package veteriapp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class VeteriApp {

    /**
     * Método principal que inicia la aplicación VeteriApp.
     * Configura el menú principal y maneja la navegación a los submenús de gestión.
     *
     * @param args Argumentos de la línea de comandos (no se utilizan en esta aplicación).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Inicialización de los objetos DAO para cada entidad
        MascotaDAO mascotaDAO = new MascotaDAO();
        DuenoDAO duenoDAO = new DuenoDAO();
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        CitaDAO citaDAO = new CitaDAO();
        TratamientoDAO tratamientoDAO = new TratamientoDAO();

        int opcion;
        do {
            // Mostrar el menú principal
            System.out.println("\n--- Menú Principal VeteriApp ---");
            System.out.println("1. Gestión de Mascotas");
            System.out.println("2. Gestión de Dueños");
            System.out.println("3. Gestión de Veterinarios");
            System.out.println("4. Gestión de Citas");
            System.out.println("5. Gestión de Tratamientos");
            System.out.println("6. Módulo Memorial"); // Nueva opción para el memorial
            System.out.println("7. Salir");
            System.out.print("Elige una opción: ");

            // Validar que la entrada sea un número entero
            while (!scanner.hasNextInt()) {
                System.out.println("Introduce un número válido.");
                scanner.next(); // Consumir la entrada no válida
                System.out.print("Elige una opción: ");
            }
            opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea pendiente

            // Navegar a la opción seleccionada
            switch (opcion) {
                case 1 -> menuMascotas(scanner, mascotaDAO);
                case 2 -> menuDuenos(scanner, duenoDAO);
                case 3 -> menuVeterinarios(scanner, veterinarioDAO);
                case 4 -> menuCitas(scanner, citaDAO);
                case 5 -> menuTratamientos(scanner, tratamientoDAO);
                case 6 -> menuMemorial(scanner, mascotaDAO, duenoDAO, citaDAO, tratamientoDAO); // Nuevo menú memorial
                case 7 -> System.out.println("Saliendo de VeteriApp...");
                default -> System.out.println("Opción no válida. Por favor, elige un número entre 1 y 7.");
            }

        } while (opcion != 7); // El bucle continúa hasta que el usuario elige salir

        scanner.close(); // Cerrar el scanner al finalizar la aplicación
    }

    // ----------------------------
    // SUBMENÚ: GESTIÓN DE MASCOTAS
    // ----------------------------

    /**
     * Muestra el submenú de gestión de mascotas y permite realizar operaciones CRUD.
     *
     * @param scanner Objeto Scanner para la entrada de usuario.
     * @param dao     Objeto MascotaDAO para interactuar con la base de datos de mascotas.
     */
    private static void menuMascotas(Scanner scanner, MascotaDAO dao) {
        System.out.println("\n--- Gestión de Mascotas ---");
        System.out.println("1. Listar mascotas");
        System.out.println("2. Añadir mascota");
        System.out.println("3. Modificar mascota");
        System.out.println("4. Eliminar mascota");
        System.out.print("Opción: ");
        int op;
        try {
            op = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea pendiente
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, introduce un número.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return;
        }

        switch (op) {
            case 1 -> {
                System.out.println("\n--- Listado de Mascotas ---");
                dao.obtenerTodasLasMascotas().forEach(System.out::println);
            }
            case 2 -> {
                System.out.println("\n--- Añadir Nueva Mascota ---");
                System.out.print("Nombre: ");
                String nombre = scanner.nextLine();
                System.out.print("Especie: ");
                String especie = scanner.nextLine();
                System.out.print("Raza: ");
                String raza = scanner.nextLine();

                LocalDate fechaNacimiento = null;
                System.out.print("Fecha de Nacimiento (YYYY-MM-DD, dejar vacío si desconocido): ");
                String fechaNacimientoStr = scanner.nextLine();
                if (!fechaNacimientoStr.isEmpty()) {
                    try {
                        fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.println("Formato de fecha no válido. Se usará null para la fecha de nacimiento.");
                    }
                }

                double peso;
                try {
                    System.out.print("Peso (kg): ");
                    peso = scanner.nextDouble();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el peso. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }

                EstadoMascota estado = EstadoMascota.ACTIVA; // Por defecto al añadir es ACTIVA
                System.out.print("DNI del dueño: ");
                String dniDueno = scanner.nextLine();

                String notasMemorial = null; // Al añadir, las notas memorial son null por defecto.
                LocalDate fechaFallecimiento = null; // Al añadir, la fecha de fallecimiento es null por defecto.

                // Uso del constructor actualizado de Mascota
                Mascota m = new Mascota(0, nombre, especie, raza, fechaNacimiento, peso, estado, fechaFallecimiento, dniDueno, notasMemorial);
                dao.insertarMascota(m);
            }
            case 3 -> {
                System.out.println("\n--- Modificar Mascota ---");
                System.out.print("ID de mascota a modificar: ");
                int id;
                try {
                    id = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }

                // Obtener la mascota existente para precargar datos y no pedir todo de nuevo
                Mascota mascotaExistente = dao.obtenerMascotaPorId(id);
                if (mascotaExistente == null) {
                    System.out.println("Mascota con ID " + id + " no encontrada.");
                    return;
                }

                System.out.print("Nuevo nombre (" + mascotaExistente.getNombre() + "): ");
                String nombre = scanner.nextLine();
                if (nombre.isEmpty()) nombre = mascotaExistente.getNombre();

                System.out.print("Nueva especie (" + mascotaExistente.getEspecie() + "): ");
                String especie = scanner.nextLine();
                if (especie.isEmpty()) especie = mascotaExistente.getEspecie();

                System.out.print("Nueva raza (" + mascotaExistente.getRaza() + "): ");
                String raza = scanner.nextLine();
                if (raza.isEmpty()) raza = mascotaExistente.getRaza();

                LocalDate fechaNacimiento = mascotaExistente.getFechaNacimiento();
                System.out.print("Nueva Fecha de Nacimiento (YYYY-MM-DD, actual: " + (fechaNacimiento != null ? fechaNacimiento : "N/A") + ", dejar vacío para mantener): ");
                String fechaNacimientoStr = scanner.nextLine();
                if (!fechaNacimientoStr.isEmpty()) {
                    try {
                        fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.println("Formato de fecha no válido. Se mantendrá la fecha de nacimiento actual.");
                    }
                }

                double peso = mascotaExistente.getPeso();
                System.out.print("Nuevo peso (kg) (" + mascotaExistente.getPeso() + "): ");
                String pesoStr = scanner.nextLine();
                if (!pesoStr.isEmpty()) {
                    try {
                        peso = Double.parseDouble(pesoStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada no válida para el peso. Se mantendrá el peso actual.");
                    }
                }

                EstadoMascota estado = mascotaExistente.getEstado();
                System.out.print("Nuevo estado (ACTIVA/FALLECIDA, actual: " + estado + "): ");
                String estadoStr = scanner.nextLine();
                if (!estadoStr.isEmpty()) {
                    try {
                        estado = EstadoMascota.valueOf(estadoStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Estado no válido. Debe ser ACTIVA o FALLECIDA. Se mantendrá el estado actual.");
                    }
                }

                LocalDate fechaFallecimiento = mascotaExistente.getFechaFallecimiento();
                if (estado == EstadoMascota.FALLECIDA && fechaFallecimiento == null) {
                    System.out.print("Fecha de Fallecimiento (YYYY-MM-DD, dejar vacío para hoy): ");
                    String fechaFallecimientoStr = scanner.nextLine();
                    if (!fechaFallecimientoStr.isEmpty()) {
                        try {
                            fechaFallecimiento = LocalDate.parse(fechaFallecimientoStr);
                        } catch (java.time.format.DateTimeParseException e) {
                            System.out.println("Formato de fecha no válido. Se usará la fecha actual si no se proporciona.");
                            fechaFallecimiento = LocalDate.now();
                        }
                    } else {
                        fechaFallecimiento = LocalDate.now(); // Si el estado es fallecida y no se da fecha, se asume hoy.
                    }
                } else if (estado == EstadoMascota.ACTIVA) {
                    fechaFallecimiento = null; // Si vuelve a activa, se elimina la fecha de fallecimiento
                }


                System.out.print("Nuevo DNI del dueño (" + mascotaExistente.getDniDueno() + "): ");
                String dniDueno = scanner.nextLine();
                if (dniDueno.isEmpty()) dniDueno = mascotaExistente.getDniDueno();

                String notasMemorial = mascotaExistente.getNotasMemorial();
                System.out.print("Notas Memorial (actual: " + (notasMemorial != null ? notasMemorial : "N/A") + ", dejar vacío para mantener): ");
                String notasMemorialStr = scanner.nextLine();
                if (!notasMemorialStr.isEmpty()) {
                    notasMemorial = notasMemorialStr;
                }

                // Uso del constructor actualizado de Mascota
                Mascota m = new Mascota(id, nombre, especie, raza, fechaNacimiento, peso, estado, fechaFallecimiento, dniDueno, notasMemorial);
                dao.modificarMascota(m);
            }
            case 4 -> {
                System.out.println("\n--- Eliminar Mascota ---");
                System.out.print("ID de mascota a eliminar: ");
                int id;
                try {
                    id = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }
                dao.eliminarMascota(id);
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    // ----------------------------
    // SUBMENÚ: GESTIÓN DE DUEÑOS
    // ----------------------------

    /**
     * Muestra el submenú de gestión de dueños y permite realizar operaciones CRUD.
     *
     * @param scanner Objeto Scanner para la entrada de usuario.
     * @param dao     Objeto DuenoDAO para interactuar con la base de datos de dueños.
     */
    private static void menuDuenos(Scanner scanner, DuenoDAO dao) {
        System.out.println("\n--- Gestión de Dueños ---");
        System.out.println("1. Listar dueños");
        System.out.println("2. Añadir dueño");
        System.out.println("3. Modificar dueño");
        System.out.println("4. Eliminar dueño");
        System.out.print("Opción: ");
        int op;
        try {
            op = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea pendiente
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, introduce un número.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return;
        }

        switch (op) {
            case 1 -> {
                System.out.println("\n--- Listado de Dueños ---");
                dao.obtenerTodosLosDuenos().forEach(System.out::println);
            }
            case 2 -> {
                System.out.println("\n--- Añadir Nuevo Dueño ---");
                System.out.print("DNI: ");
                String dni = scanner.nextLine();
                System.out.print("Nombre: ");
                String nombre = scanner.nextLine();
                System.out.print("Apellidos: ");
                String apellidos = scanner.nextLine();
                System.out.print("Teléfono: ");
                String telefono = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Dirección: ");
                String direccion = scanner.nextLine();
                Dueno d = new Dueno(dni, nombre, apellidos, telefono, email, direccion);
                dao.insertarDueno(d);
            }
            case 3 -> {
                System.out.println("\n--- Modificar Dueño ---");
                System.out.print("DNI del dueño a modificar: ");
                String dni = scanner.nextLine();
                // Obtener el dueño existente para precargar datos y no pedir todo de nuevo
                Dueno duenoExistente = dao.obtenerDuenoPorDni(dni);
                if (duenoExistente == null) {
                    System.out.println("Dueño con DNI " + dni + " no encontrado.");
                    return;
                }

                System.out.print("Nuevo nombre (" + duenoExistente.getNombre() + "): ");
                String nombre = scanner.nextLine();
                if (nombre.isEmpty()) nombre = duenoExistente.getNombre();

                System.out.print("Nuevos apellidos (" + duenoExistente.getApellidos() + "): ");
                String apellidos = scanner.nextLine();
                if (apellidos.isEmpty()) apellidos = duenoExistente.getApellidos();

                System.out.print("Nuevo teléfono (" + duenoExistente.getTelefono() + "): ");
                String telefono = scanner.nextLine();
                if (telefono.isEmpty()) telefono = duenoExistente.getTelefono();

                System.out.print("Nuevo email (" + duenoExistente.getEmail() + "): ");
                String email = scanner.nextLine();
                if (email.isEmpty()) email = duenoExistente.getEmail();

                System.out.print("Nueva dirección (" + duenoExistente.getDireccion() + "): ");
                String direccion = scanner.nextLine();
                if (direccion.isEmpty()) direccion = duenoExistente.getDireccion();

                Dueno d = new Dueno(dni, nombre, apellidos, telefono, email, direccion);
                dao.modificarDueno(d);
            }
            case 4 -> {
                System.out.println("\n--- Eliminar Dueño ---");
                System.out.print("DNI del dueño a eliminar: ");
                String dni = scanner.nextLine();
                dao.eliminarDueno(dni);
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    // ----------------------------
    // SUBMENÚ: GESTIÓN DE VETERINARIOS
    // ----------------------------

    /**
     * Muestra el submenú de gestión de veterinarios y permite realizar operaciones CRUD.
     *
     * @param scanner Objeto Scanner para la entrada de usuario.
     * @param dao     Objeto VeterinarioDAO para interactuar con la base de datos de veterinarios.
     */
    private static void menuVeterinarios(Scanner scanner, VeterinarioDAO dao) {
        System.out.println("\n--- Gestión de Veterinarios ---");
        System.out.println("1. Listar veterinarios");
        System.out.println("2. Añadir veterinario");
        System.out.println("3. Modificar veterinario");
        System.out.println("4. Eliminar veterinario");
        System.out.print("Opción: ");
        int op;
        try {
            op = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea pendiente
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, introduce un número.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return;
        }

        switch (op) {
            case 1 -> {
                System.out.println("\n--- Listado de Veterinarios ---");
                dao.obtenerTodosLosVeterinarios().forEach(System.out::println);
            }
            case 2 -> {
                System.out.println("\n--- Añadir Nuevo Veterinario ---");
                System.out.print("DNI: ");
                String dni = scanner.nextLine();
                System.out.print("Nombre: ");
                String nombre = scanner.nextLine();
                System.out.print("Apellidos: ");
                String apellidos = scanner.nextLine();
                System.out.print("Especialidad: ");
                String esp = scanner.nextLine();
                System.out.print("Teléfono: ");
                String tel = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                Veterinario v = new Veterinario(dni, nombre, apellidos, esp, tel, email);
                dao.insertarVeterinario(v);
            }
            case 3 -> {
                System.out.println("\n--- Modificar Veterinario ---");
                System.out.print("DNI del veterinario a modificar: ");
                String dni = scanner.nextLine();
                System.out.print("Nuevo nombre: ");
                String nombre = scanner.nextLine();
                System.out.print("Nuevos apellidos: ");
                String apellidos = scanner.nextLine();
                System.out.print("Nueva especialidad: ");
                String esp = scanner.nextLine();
                System.out.print("Nuevo teléfono: ");
                String tel = scanner.nextLine();
                System.out.print("Nuevo email: ");
                String email = scanner.nextLine();
                Veterinario v = new Veterinario(dni, nombre, apellidos, esp, tel, email);
                dao.modificarVeterinario(v);
            }
            case 4 -> {
                System.out.println("\n--- Eliminar Veterinario ---");
                System.out.print("DNI del veterinario a eliminar: ");
                String dni = scanner.nextLine();
                dao.eliminarVeterinario(dni);
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    // ----------------------------
    // SUBMENÚ: GESTIÓN DE CITAS
    // ----------------------------

    /**
     * Muestra el submenú de gestión de citas y permite realizar operaciones CRUD.
     *
     * @param scanner Objeto Scanner para la entrada de usuario.
     * @param dao     Objeto CitaDAO para interactuar con la base de datos de citas.
     */
    private static void menuCitas(Scanner scanner, CitaDAO dao) {
        System.out.println("\n--- Gestión de Citas ---");
        System.out.println("1. Listar citas");
        System.out.println("2. Añadir cita");
        System.out.println("3. Modificar cita");
        System.out.println("4. Eliminar cita");
        System.out.print("Opción: ");
        int op;
        try {
            op = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea pendiente
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, introduce un número.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return;
        }

        switch (op) {
            case 1 -> {
                System.out.println("\n--- Listado de Citas ---");
                dao.obtenerTodasLasCitas().forEach(System.out::println);
            }
            case 2 -> {
                System.out.println("\n--- Añadir Nueva Cita ---");
                LocalDateTime fechaHora;
                try {
                    System.out.print("Fecha y hora (YYYY-MM-DDTHH:MM): ");
                    fechaHora = LocalDateTime.parse(scanner.nextLine());
                } catch (java.time.format.DateTimeParseException e) {
                    System.out.println("Formato de fecha y hora no válido. Usa YYYY-MM-DDTHH:MM.");
                    return;
                }
                System.out.print("Motivo: ");
                String motivo = scanner.nextLine();

                EstadoCita estado;
                System.out.print("Estado (PENDIENTE/REALIZADA/CANCELADA): ");
                try {
                    estado = EstadoCita.valueOf(scanner.nextLine().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Estado no válido. Debe ser PENDIENTE, REALIZADA o CANCELADA.");
                    return;
                }

                System.out.print("ID mascota: ");
                int idMascota;
                try {
                    idMascota = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de mascota. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }
                Cita cita = new Cita(0, fechaHora, motivo, estado, idMascota);
                dao.insertarCita(cita);
            }
            case 3 -> {
                System.out.println("\n--- Modificar Cita ---");
                System.out.print("ID de cita a modificar: ");
                int id;
                try {
                    id = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de cita. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }

                LocalDateTime fechaHora;
                try {
                    System.out.print("Nueva fecha y hora (YYYY-MM-DDTHH:MM): ");
                    fechaHora = LocalDateTime.parse(scanner.nextLine());
                } catch (java.time.format.DateTimeParseException e) {
                    System.out.println("Formato de fecha y hora no válido. Usa YYYY-MM-DDTHH:MM.");
                    return;
                }
                System.out.print("Nuevo motivo: ");
                String motivo = scanner.nextLine();

                EstadoCita estado;
                System.out.print("Nuevo estado (PENDIENTE/REALIZADA/CANCELADA): ");
                try {
                    estado = EstadoCita.valueOf(scanner.nextLine().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Estado no válido. Debe ser PENDIENTE, REALIZADA o CANCELADA.");
                    return;
                }

                System.out.print("Nuevo ID mascota: ");
                int idMascota;
                try {
                    idMascota = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de mascota. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }
                Cita cita = new Cita(id, fechaHora, motivo, estado, idMascota);
                dao.modificarCita(cita);
            }
            case 4 -> {
                System.out.println("\n--- Eliminar Cita ---");
                System.out.print("ID de cita a eliminar: ");
                int id;
                try {
                    id = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de cita. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }
                dao.eliminarCita(id);
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    // ----------------------------
    // SUBMENÚ: GESTIÓN DE TRATAMIENTOS
    // ----------------------------

    /**
     * Muestra el submenú de gestión de tratamientos y permite realizar operaciones CRUD.
     *
     * @param scanner Objeto Scanner para la entrada de usuario.
     * @param dao     Objeto TratamientoDAO para interactuar con la base de datos de tratamientos.
     */
    private static void menuTratamientos(Scanner scanner, TratamientoDAO dao) {
        System.out.println("\n--- Gestión de Tratamientos ---");
        System.out.println("1. Listar tratamientos");
        System.out.println("2. Añadir tratamiento");
        System.out.println("3. Modificar tratamiento");
        System.out.println("4. Eliminar tratamiento");
        System.out.print("Opción: ");
        int op;
        try {
            op = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea pendiente
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, introduce un número.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return;
        }

        switch (op) {
            case 1 -> {
                System.out.println("\n--- Listado de Tratamientos ---");
                dao.obtenerTodos().forEach(System.out::println);
            }
            case 2 -> {
                System.out.println("\n--- Añadir Nuevo Tratamiento ---");
                System.out.print("Tipo: ");
                String tipo = scanner.nextLine();
                System.out.print("Descripción: ");
                String desc = scanner.nextLine();
                LocalDate fecha;
                try {
                    System.out.print("Fecha (YYYY-MM-DD): ");
                    fecha = LocalDate.parse(scanner.nextLine());
                } catch (java.time.format.DateTimeParseException e) {
                    System.out.println("Formato de fecha no válido. Usa YYYY-MM-DD.");
                    return;
                }
                System.out.print("Observaciones: ");
                String obs = scanner.nextLine();
                System.out.print("ID mascota: ");
                int idMascota;
                try {
                    idMascota = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de mascota. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }
                Tratamiento t = new Tratamiento(0, tipo, desc, fecha, obs, idMascota);
                dao.insertarTratamiento(t);
            }
            case 3 -> {
                System.out.println("\n--- Modificar Tratamiento ---");
                System.out.print("ID de tratamiento a modificar: ");
                int id;
                try {
                    id = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de tratamiento. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }

                System.out.print("Nuevo tipo: ");
                String tipo = scanner.nextLine();
                System.out.print("Nueva descripción: ");
                String desc = scanner.nextLine();
                LocalDate fecha;
                try {
                    System.out.print("Nueva fecha (YYYY-MM-DD): ");
                    fecha = LocalDate.parse(scanner.nextLine());
                } catch (java.time.format.DateTimeParseException e) {
                    System.out.println("Formato de fecha no válido. Usa YYYY-MM-DD.");
                    return;
                }
                System.out.print("Nuevas observaciones: ");
                String obs = scanner.nextLine();
                System.out.print("Nuevo ID mascota: ");
                int idMascota;
                try {
                    idMascota = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de mascota. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }
                Tratamiento t = new Tratamiento(id, tipo, desc, fecha, obs, idMascota);
                dao.modificarTratamiento(t);
            }
            case 4 -> {
                System.out.println("\n--- Eliminar Tratamiento ---");
                System.out.print("ID de tratamiento a eliminar: ");
                int id;
                try {
                    id = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea pendiente
                } catch (InputMismatchException e) {
                    System.out.println("Entrada no válida para el ID de tratamiento. Por favor, introduce un número.");
                    scanner.nextLine(); // Consumir la entrada incorrecta
                    return;
                }
                dao.eliminarTratamiento(id);
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    // ----------------------------
    // NUEVO SUBMENÚ: MÓDULO MEMORIAL
    // ----------------------------

    /**
     * Muestra el submenú del módulo memorial para mascotas fallecidas.
     * Permite listar, ver detalles y modificar notas memorial de mascotas fallecidas.
     *
     * @param scanner         Objeto Scanner para la entrada de usuario.
     * @param mascotaDAO      Objeto MascotaDAO para interactuar con datos de mascotas.
     * @param duenoDAO        Objeto DuenoDAO para obtener datos del dueño.
     * @param citaDAO         Objeto CitaDAO para obtener historial de citas.
     * @param tratamientoDAO  Objeto TratamientoDAO para obtener historial de tratamientos.
     */
    private static void menuMemorial(Scanner scanner, MascotaDAO mascotaDAO, DuenoDAO duenoDAO, CitaDAO citaDAO, TratamientoDAO tratamientoDAO) {
        int opcionMemorial;
        do {
            System.out.println("\n--- Módulo Memorial ---");
            System.out.println("1. Listar Mascotas Fallecidas");
            System.out.println("2. Ver Detalles Memorial de Mascota");
            System.out.println("3. Modificar Notas Memorial");
            System.out.println("4. Volver al Menú Principal");
            System.out.print("Elige una opción: ");

            try {
                opcionMemorial = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                scanner.nextLine(); // Consumir la entrada incorrecta
                opcionMemorial = 0; // Para que el bucle continúe
                continue;
            }

            switch (opcionMemorial) {
                case 1 -> {
                    System.out.println("\n--- Listado de Mascotas Fallecidas ---");
                    List<Mascota> mascotasFallecidas = mascotaDAO.obtenerTodasLasMascotas().stream()
                                                            .filter(m -> m.getEstado() == EstadoMascota.FALLECIDA)
                                                            .toList();
                    if (mascotasFallecidas.isEmpty()) {
                        System.out.println("No hay mascotas registradas como fallecidas.");
                    } else {
                        mascotasFallecidas.forEach(mascota -> {
                            System.out.println("ID: " + mascota.getIdMascota() +
                                               ", Nombre: " + mascota.getNombre() +
                                               ", Especie: " + mascota.getEspecie() +
                                               ", Fecha Fallecimiento: " + mascota.getFechaFallecimiento());
                        });
                    }
                }
                case 2 -> {
                    System.out.println("\n--- Ver Detalles Memorial de Mascota ---");
                    System.out.print("Introduce el ID de la mascota fallecida: ");
                    int idMascota;
                    try {
                        idMascota = scanner.nextInt();
                        scanner.nextLine(); // Consumir el salto de línea
                    } catch (InputMismatchException e) {
                        System.out.println("ID no válido. Por favor, introduce un número entero.");
                        scanner.nextLine(); // Consumir la entrada incorrecta
                        break;
                    }

                    Mascota mascota = mascotaDAO.obtenerMascotaPorId(idMascota);

                    if (mascota == null) {
                        System.out.println("Mascota con ID " + idMascota + " no encontrada.");
                    } else if (mascota.getEstado() != EstadoMascota.FALLECIDA) {
                        System.out.println("La mascota con ID " + idMascota + " no está registrada como fallecida. Estado actual: " + mascota.getEstado());
                    } else {
                        System.out.println("\n--- Detalles Memorial para " + mascota.getNombre() + " (ID: " + mascota.getIdMascota() + ") ---");
                        System.out.println("Especie: " + mascota.getEspecie());
                        System.out.println("Raza: " + mascota.getRaza());
                        System.out.println("Fecha de Nacimiento: " + (mascota.getFechaNacimiento() != null ? mascota.getFechaNacimiento() : "N/A"));
                        System.out.println("Fecha de Fallecimiento: " + (mascota.getFechaFallecimiento() != null ? mascota.getFechaFallecimiento() : "N/A"));
                        System.out.println("Peso: " + mascota.getPeso() + " kg");

                        // Información del Dueño
                        if (mascota.getDniDueno() != null && !mascota.getDniDueno().isEmpty()) {
                            Dueno dueno = duenoDAO.obtenerDuenoPorDni(mascota.getDniDueno());
                            if (dueno != null) {
                                System.out.println("--- Información del Dueño ---");
                                System.out.println("DNI Dueño: " + dueno.getDniDueno());
                                System.out.println("Nombre Dueño: " + dueno.getNombre() + " " + dueno.getApellidos());
                                System.out.println("Teléfono Dueño: " + dueno.getTelefono());
                                System.out.println("Email Dueño: " + dueno.getEmail());
                            } else {
                                System.out.println("DNI del dueño: " + mascota.getDniDueno() + " (Dueño no encontrado en la base de datos)");
                            }
                        } else {
                            System.out.println("DNI del Dueño: No especificado.");
                        }

                        // Notas Memorial
                        System.out.println("\n--- Notas Memorial ---");
                        System.out.println(mascota.getNotasMemorial() != null && !mascota.getNotasMemorial().isEmpty()
                                           ? mascota.getNotasMemorial()
                                           : "No hay notas memorial registradas para esta mascota.");

                        // Historial de Citas
                        System.out.println("\n--- Historial de Citas ---");
                        List<Cita> citas = citaDAO.obtenerTodasLasCitas().stream()
                                            .filter(c -> c.getIdMascota() == idMascota)
                                            .toList();
                        if (citas.isEmpty()) {
                            System.out.println("No hay citas registradas para esta mascota.");
                        } else {
                            citas.forEach(System.out::println);
                        }

                        // Historial de Tratamientos
                        System.out.println("\n--- Historial de Tratamientos ---");
                        List<Tratamiento> tratamientos = tratamientoDAO.obtenerTodos().stream()
                                                            .filter(t -> t.getIdMascota() == idMascota)
                                                            .toList();
                        if (tratamientos.isEmpty()) {
                            System.out.println("No hay tratamientos registrados para esta mascota.");
                        } else {
                            tratamientos.forEach(System.out::println);
                        }
                    }
                }
                case 3 -> {
                    System.out.println("\n--- Modificar Notas Memorial ---");
                    System.out.print("Introduce el ID de la mascota fallecida para modificar sus notas: ");
                    int idMascota;
                    try {
                        idMascota = scanner.nextInt();
                        scanner.nextLine(); // Consumir el salto de línea
                    } catch (InputMismatchException e) {
                        System.out.println("ID no válido. Por favor, introduce un número entero.");
                        scanner.nextLine(); // Consumir la entrada incorrecta
                        break;
                    }

                    Mascota mascota = mascotaDAO.obtenerMascotaPorId(idMascota);

                    if (mascota == null) {
                        System.out.println("Mascota con ID " + idMascota + " no encontrada.");
                    } else if (mascota.getEstado() != EstadoMascota.FALLECIDA) {
                        System.out.println("La mascota con ID " + idMascota + " no está registrada como fallecida. No se pueden modificar notas memorial.");
                    } else {
                        System.out.println("Notas memorial actuales para " + mascota.getNombre() + ":");
                        System.out.println(mascota.getNotasMemorial() != null && !mascota.getNotasMemorial().isEmpty()
                                           ? mascota.getNotasMemorial()
                                           : "No hay notas actuales.");
                        System.out.print("Introduce las nuevas notas memorial (dejar vacío para borrar, o escribir 'N/A' para no establecer): ");
                        String nuevasNotas = scanner.nextLine();

                        if (nuevasNotas.equalsIgnoreCase("N/A") || nuevasNotas.isEmpty()) {
                            mascota.setNotasMemorial(null); // Establecer como null si el usuario quiere borrar o no establecer
                        } else {
                            mascota.setNotasMemorial(nuevasNotas);
                        }

                        // Es importante usar el método modificarMascota que actualiza todos los campos
                        // de la Mascota, incluyendo las notas memorial.
                        mascotaDAO.modificarMascota(mascota);
                        System.out.println("Notas memorial actualizadas correctamente.");
                    }
                }
                case 4 -> System.out.println("Volviendo al Menú Principal...");
                default -> System.out.println("Opción no válida. Por favor, elige un número entre 1 y 4.");
            }
        } while (opcionMemorial != 4);
    }

}