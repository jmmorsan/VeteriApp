/**
 * Clase que representa un tratamiento veterinario.
 * Puede ser realizado por uno o más veterinarios a una mascota.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

import java.time.LocalDate;


public class Tratamiento {
	
	// Declaraación de variables

    private int idTratamiento;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
    private String observaciones;
    private int idMascota;
    
    /**
	 * Constructor de Invitado
	 * 
	 * @param idTratamiento		Id del tratamiento
	 * @param tipo				Tipo de tratamiento
	 * @param descripcion		Descripcion del tratamiento
	 * @param fecha				Fecha en la que se le da el tratamiento
	 * @param observaciones		Observaciones del tratamiento
	 * @param idMascota 		Id de la mascota a la que se le realiza el tratamiento
	 */

    public Tratamiento(int idTratamiento, String tipo, String descripcion,
                       LocalDate fecha, String observaciones, int idMascota) {
        this.idTratamiento = idTratamiento;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.idMascota = idMascota;
    }
    
    // Getters y setters
    
	public int getIdTratamiento() {
		return idTratamiento;
	}

	public void setIdTratamiento(int idTratamiento) {
		this.idTratamiento = idTratamiento;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public int getIdMascota() {
		return idMascota;
	}

	public void setIdMascota(int idMascota) {
		this.idMascota = idMascota;
	}
	

    @Override
    public String toString() {
        return "Tratamiento [" + tipo + ", fecha=" + fecha + ", obs=" + observaciones + "]";
    }
}
