package veteriapp;


import java.util.Scanner;

public class VeteriApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MascotaDAO mascotaDAO = new MascotaDAO();
        DuenoDAO duenoDAO = new DuenoDAO();
        VeterinarioDAO vetDAO = new VeterinarioDAO();

        int opcion;
        do {
            System.out.println("\nMenú Principal VeteriApp:");
            System.out.println("1. Ver mascotas");
            System.out.println("2. Ver dueños");
            System.out.println("3. Ver veterinarios");
            System.out.println("4. Salir");
            System.out.print("Elige opción: ");
            opcion = scanner.nextInt(); scanner.nextLine();

            switch (opcion) {
                case 1 -> mascotaDAO.obtenerTodasLasMascotas().forEach(System.out::println);
                case 2 -> duenoDAO.obtenerTodosLosDuenos().forEach(System.out::println);
                case 3 -> vetDAO.obtenerTodosLosVeterinarios().forEach(System.out::println);
                case 4 -> System.out.println("Gracias por usar VeteriApp.");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 4);

        scanner.close();
    }
}