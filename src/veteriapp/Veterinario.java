/**
 * Clase que representa un veterinario de la clínica.
 * Puede participar en citas y tratamientos de distintas mascotas.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

public class Veterinario {
    
    // Declaración de variables

    private String dniVeterinario;
    private String nombre;
    private String apellidos;
    private String especialidad;
    private String telefono;
    private String email;

    /**
     * Constructor de Veterinario
     * 
     * @param dniVeterinario DNI del veterinario
     * @param nombre         Nombre del veterinario
     * @param apellidos      Apellidos del veterinario
     * @param especialidad   Especialidad médica
     * @param telefono       Número de contacto
     * @param email          Correo electrónico
     */
    public Veterinario(String dniVeterinario, String nombre, String apellidos,
                       String especialidad, String telefono, String email) {
        this.dniVeterinario = dniVeterinario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.email = email;
    }
    
    //Getters y Setters

	public String getDniVeterinario() {
		return dniVeterinario;
	}

	public void setDniVeterinario(String dniVeterinario) {
		this.dniVeterinario = dniVeterinario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Veterinario [dniVeterinario=" + dniVeterinario + ", nombre=" + nombre + ", apellidos=" + apellidos
				+ ", especialidad=" + especialidad + ", telefono=" + telefono + ", email=" + email + "]";
	}  
}
