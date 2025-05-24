package veteriapp;

import java.time.LocalDate;

/**
 * Clase Tratamiento
 */
class Tratamiento {
    private int idTratamiento;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
    private String observaciones;
    private int idMascota;

    public Tratamiento(int idTratamiento, String tipo, String descripcion, LocalDate fecha, String observaciones, int idMascota) {
        this.idTratamiento = idTratamiento;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.idMascota = idMascota;
    }

    @Override
    public String toString() {
        return "Tratamiento: " + tipo + " el " + fecha + ". Observaciones: " + observaciones;
    }
}