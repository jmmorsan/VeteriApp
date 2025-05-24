package veteriapp;
/**
 * Clase Veterinario
 */
class Veterinario {
    private String dniVeterinario;
    private String nombre;
    private String apellidos;
    private String especialidad;
    private String telefono;
    private String email;

    public Veterinario(String dniVeterinario, String nombre, String apellidos, String especialidad, String telefono, String email) {
        this.dniVeterinario = dniVeterinario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.email = email;
    }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " - " + especialidad;
    }
}