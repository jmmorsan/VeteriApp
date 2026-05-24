package com.example.veteriapp.model;

/**
 * Enumeración de Estados de Cita Médica.
 * 
 * Gestiona el ciclo de vida de una consulta, desde su solicitud inicial
 * hasta su finalización y archivo histórico en la clínica.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public enum EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    RECHAZADA,
    CANCELADA,
    ARCHIVADA
}