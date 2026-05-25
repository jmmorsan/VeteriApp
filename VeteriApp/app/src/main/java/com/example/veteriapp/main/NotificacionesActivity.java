package com.example.veteriapp.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.veteriapp.R;
import com.example.veteriapp.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase NotificacionesActivity.
 * 
 * Centraliza la visualización y gestión de alertas del sistema.
 * Implementa una lógica de filtrado selectivo según el rol (Dueño o Clínica)
 * y una ordenación en memoria para garantizar la visibilidad incluso sin índices.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0.5 Parche de Robustez Total
 */
public class NotificacionesActivity extends AppCompatActivity {

    private LinearLayout contenedor;
    private FirebaseFirestore db;
    private String miUid, miRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_notificaciones);

        db = FirebaseFirestore.getInstance();
        miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contenedor = findViewById(R.id.contenedorNotificaciones);

        Toolbar toolbar = findViewById(R.id.toolbarNotif);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Identificación del perfil con lógica de compatibilidad
        db.collection("users").document(miUid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Object rObj = doc.get("rol");
                if (rObj instanceof String) {
                    miRol = (String) rObj;
                } else if (rObj instanceof List) {
                    List<?> roles = (List<?>) rObj;
                    if (!roles.isEmpty()) miRol = roles.get(0).toString();
                }
                
                Log.d("VeteriApp", "Notif - Rol detectado: " + miRol);
                cargarNotificaciones();
            } else {
                Toast.makeText(this, "Perfil no encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("VeteriApp", "Error al cargar rol", e);
            cargarNotificaciones();
        });

        ImageButton btnBorrarTodo = findViewById(R.id.btnBorrarTodo);
        if (btnBorrarTodo != null) {
            btnBorrarTodo.setOnClickListener(v -> mostrarDialogoBorrarTodo());
        }
    }

    private void mostrarDialogoBorrarTodo() {
        new AlertDialog.Builder(this)
                .setTitle("Vaciar Bandeja 🗑️")
                .setMessage("¿Estás seguro de que deseas eliminar todas las notificaciones?")
                .setPositiveButton("Sí, borrar", (d, w) -> borrarTodasLasNotificaciones())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarTodasLasNotificaciones() {
        String dest = ("VETERINARIO".equals(miRol) || "ADMIN".equals(miRol)) ? "CLINICA" : miUid;
        db.collection("notificaciones").whereEqualTo("uidDestinatario", dest).get().addOnSuccessListener(query -> {
            com.google.firebase.firestore.WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot doc : query) {
                batch.delete(doc.getReference());
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Bandeja vaciada", Toast.LENGTH_SHORT).show();
                Logger.log("Limpieza de notificaciones realizada por " + miUid);
                cargarNotificaciones();
            });
        });
    }

    /**
     * Recupera las notificaciones. Se elimina el orderBy por servidor para evitar
     * fallos por falta de índices y se realiza la ordenación en local.
     */
    private void cargarNotificaciones() {
        if (contenedor == null) return;
        contenedor.removeAllViews();

        String dest = ("VETERINARIO".equals(miRol) || "ADMIN".equals(miRol)) ? "CLINICA" : miUid;
        Log.d("VeteriApp", "Buscando notificaciones para destinatario: " + dest);

        db.collection("notificaciones")
                .whereEqualTo("uidDestinatario", dest)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<QueryDocumentSnapshot> docs = new ArrayList<>();
                        for (QueryDocumentSnapshot d : task.getResult()) {
                            docs.add(d);
                        }

                        // Ordenación local por timestamp (más reciente primero)
                        Collections.sort(docs, (d1, d2) -> {
                            com.google.firebase.Timestamp t1 = d1.getTimestamp("timestamp");
                            com.google.firebase.Timestamp t2 = d2.getTimestamp("timestamp");
                            if (t1 == null || t2 == null) return 0;
                            return t2.compareTo(t1);
                        });

                        if (docs.isEmpty()) {
                            TextView empty = new TextView(this);
                            empty.setText("No tienes notificaciones pendientes.");
                            empty.setGravity(Gravity.CENTER);
                            empty.setPadding(0, 50, 0, 0);
                            contenedor.addView(empty);
                        } else {
                            for (QueryDocumentSnapshot doc : docs) {
                                crearFilaNotificacion(doc.getId(), doc.getString("mensaje"), doc.getBoolean("leida"));
                            }
                        }
                    } else {
                        Log.e("VeteriApp", "Error Firestore Notif: ", task.getException());
                        Toast.makeText(this, "Error al sincronizar avisos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void crearFilaNotificacion(String idDoc, String mensaje, Boolean leida) {
        boolean isLeida = (leida != null && leida);
        
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(35, 35, 35, 35);
        tarjeta.setBackgroundResource(R.drawable.bg_notif_item);
        
        if (!isLeida) {
            tarjeta.setBackgroundColor(Color.parseColor("#E8F5E9")); // Fondo verde suave para no leídas
        } else {
            tarjeta.setBackgroundColor(Color.WHITE);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 15);
        tarjeta.setLayoutParams(params);

        TextView tv = new TextView(this);
        tv.setText(mensaje != null ? mensaje : "Aviso sin contenido");
        tv.setTextSize(15);
        tv.setTextColor(Color.BLACK);
        tarjeta.addView(tv);

        tarjeta.setOnClickListener(v -> {
            db.collection("notificaciones").document(idDoc).update("leida", true).addOnSuccessListener(aVoid -> {
                tarjeta.setBackgroundColor(Color.WHITE);
                // Si estamos en dashboard, el punto rojo se actualizará solo por el listener de allí
            });
        });

        contenedor.addView(tarjeta);
    }
}