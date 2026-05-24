package com.example.veteriapp.model;

import java.util.List;

/**
 * Clase de Modelo para Datos Curiosos (Dog API).
 * 
 * Estructura de datos compatible con la respuesta JSON de la API externa
 * utilizada para obtener curiosidades sobre mascotas.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class AnimalFact {

    private List<FactData> data;

    /**
     * Extrae el contenido textual de la curiosidad desde la estructura anidada.
     * 
     * @return Cadena con el dato curioso en inglés.
     */
    public String getFact() {
        if (data != null && !data.isEmpty()) {
            return data.get(0).getAttributes().getBody();
        }
        return null;
    }

    /**
     * Subclase interna para el mapeo de atributos del JSON.
     */
    public static class FactData {
        private Attributes attributes;
        public Attributes getAttributes() { return attributes; }
    }

    /**
     * Subclase interna para el mapeo del cuerpo del mensaje.
     */
    public static class Attributes {
        private String body;
        public String getBody() { return body; }
    }
}