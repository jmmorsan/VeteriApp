package com.example.veteriapp.model;

import com.google.firebase.Timestamp;

/**
 * Clase de Modelo para Mensajes de Chat.
 * 
 * Estructura de datos que representa un intercambio de texto en tiempo real.
 * Compatible con la serialización automática de Firebase Firestore.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class Mensaje {

    private String uidRemitente;
    private String texto;
    private String idSala;
    private Timestamp timestamp;

    /**
     * Constructor vacío requerido por Firestore para deserialización POJO.
     */
    public Mensaje() {}

    /**
     * Constructor parametrizado para la creación manual de mensajes.
     */
    public Mensaje(String uidRemitente, String texto, String idSala) {
        this.uidRemitente = uidRemitente;
        this.texto = texto;
        this.idSala = idSala;
        this.timestamp = Timestamp.now();
    }

    // --- MÉTODOS ACCESORES (GETTERS Y SETTERS) ---

    public String getUidRemitente() { return uidRemitente; }
    public void setUidRemitente(String uidRemitente) { this.uidRemitente = uidRemitente; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getIdSala() { return idSala; }
    public void setIdSala(String idSala) { this.idSala = idSala; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}