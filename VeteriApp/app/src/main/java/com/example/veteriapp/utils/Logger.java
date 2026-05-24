package com.example.veteriapp.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase Logger.
 * 
 * Centraliza el registro de eventos de auditoría técnica y administrativa.
 * Implementa la persistencia de logs en Firestore con identificadores secuenciales id_log
 * y marcas de tiempo precisas para el control de actividad del sistema.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class Logger {

    /**
     * Registra un mensaje de evento en la colección global de logs.
     * Calcula automáticamente el siguiente ID secuencial disponible.
     * 
     * @param mensaje Descripción de la acción o evento ocurrido.
     */
    public static void log(String mensaje) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consulta de agregación para ID de log secuencial
        db.collection("logs").orderBy("id_log", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(snap -> {
                    int idCalc = 1;
                    if (!snap.isEmpty()) {
                        Number ult = snap.getDocuments().get(0).getLong("id_log");
                        if (ult != null) idCalc = ult.intValue() + 1;
                    }

                    Map<String, Object> l = new HashMap<>();
                    l.put("id_log", idCalc);
                    l.put("mensaje", mensaje);
                    l.put("timestamp", Timestamp.now());

                    db.collection("logs").add(l);
                });
    }
}