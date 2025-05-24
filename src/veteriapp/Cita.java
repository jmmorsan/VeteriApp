package veteriapp;

import java.time.LocalDateTime;

/**
 * Clase Cita
 */
class Cita {
    private int idCita;
    private LocalDateTime fechaHora;
    private String motivo;
    private EstadoCita estado;
    private int idMascota;

    public Cita(int idCita, LocalDateTime fechaHora, String motivo, EstadoCita estado, int idMascota) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
        this.idMascota = idMascota;
    }

    @Override
    public String toString() {
        return "Cita " + idCita + " - " + motivo + " a las " + fechaHora + " (" + estado + ")";
    }
}