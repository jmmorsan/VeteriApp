package com.example.veteriapp.model;

/**
 * Modelo de datos para las Citas Médicas.
 */
public class Cita {

    public enum EstadoCita {
        PENDIENTE, ACEPTADA, RECHAZADA, MODIFICADA, CONFIRMADA, CANCELADA, ARCHIVADA
    }

    public enum TipoCita {
        POR_DEFINIR, CONSULTA, VACUNACION, CIRUGIA, REVISION, URGENCIA, PELUQUERIA, FINALIZADA
    }

    private String id;
    private String uidUsuario;
    private String nombreUsuario;
    private String nombreMascota;
    private String motivoDueño;
    private String tipoAsignado;
    private String fechaHora;
    private String estado;
    private String notaVeterinario;

    public Cita() {}

    public Cita(String uidUsuario, String nombreUsuario, String nombreMascota, String motivoDueño, String fechaHora) {
        this.uidUsuario = uidUsuario;
        this.nombreUsuario = nombreUsuario;
        this.nombreMascota = nombreMascota;
        this.motivoDueño = motivoDueño;
        this.fechaHora = fechaHora;
        this.estado = EstadoCita.PENDIENTE.name();
        this.tipoAsignado = TipoCita.POR_DEFINIR.name();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUidUsuario() { return uidUsuario; }
    public void setUidUsuario(String uidUsuario) { this.uidUsuario = uidUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getMotivoDueño() { return motivoDueño; }
    public void setMotivoDueño(String motivoDueño) { this.motivoDueño = motivoDueño; }

    public String getTipoAsignado() { return tipoAsignado; }
    public void setTipoAsignado(String tipoAsignado) { this.tipoAsignado = tipoAsignado; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotaVeterinario() { return notaVeterinario; }
    public void setNotaVeterinario(String notaVeterinario) { this.notaVeterinario = notaVeterinario; }
}