/**
 * Clase que representa una cita veterinaria en la clínica.
 * Relaciona una mascota con uno o más veterinarios en una fecha concreta.
 * 
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

import java.time.LocalDateTime;

enum EstadoCita { PENDIENTE, REALIZADA, CANCELADA }

public class Cita {

    // Declaración de variables

    private int idCita;
    private LocalDateTime fechaHora;
    private String motivo;
    private EstadoCita estado;
    private int idMascota;

    /**
     * Constructor de Cita
     * 
     * @param idCita      Id de la cita
     * @param fechaHora   Fecha y hora de la cita
     * @param motivo      Motivo de la consulta
     * @param estado      Estado de la cita (pendiente, realizada, cancelada)
     * @param idMascota   Id de la mascota que tiene la cita
     */
    public Cita(int idCita, LocalDateTime fechaHora, String motivo, EstadoCita estado, int idMascota) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
        this.idMascota = idMascota;
    }
    
    //Getter y Setters

	public int getIdCita() {
		return idCita;
	}

	public void setIdCita(int idCita) {
		this.idCita = idCita;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public EstadoCita getEstado() {
		return estado;
	}

	public void setEstado(EstadoCita estado) {
		this.estado = estado;
	}

	public int getIdMascota() {
		return idMascota;
	}

	public void setIdMascota(int idMascota) {
		this.idMascota = idMascota;
	}

	@Override
	public String toString() {
		return "Cita [idCita=" + idCita + ", fechaHora=" + fechaHora + ", motivo=" + motivo + ", estado=" + estado
				+ ", idMascota=" + idMascota + "]";
	}
}
