package com.example.veteriapp.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

import java.util.List;

/**
 * Clase NotificacionesActivity.
 * 
 * Centraliza la visualización y gestión de alertas del sistema.
 * Implementa una lógica de filtrado selectivo según el rol (Dueño o Clínica)
 * y permite la depuración masiva de avisos mediante operaciones de lote (WriteBatch).
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class NotificacionesActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedor;
    
    // --- INSTANCIAS DE FIREBASE Y ESTADO ---
    private FirebaseFirestore db;
    private String miUid, miRol;

    /**
     * Inicializa la bandeja de notificaciones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Activación del Modo Inmersivo Total
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_notificaciones);

        db = FirebaseFirestore.getInstance();
        miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contenedor = findViewById(R.id.contenedorNotificaciones);

        // Configuración de Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarNotif);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Identificación del perfil para filtrado de mensajes
        db.collection("users").document(miUid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Object rObj = doc.get("rol");
                if (rObj instanceof String) miRol = (String) rObj;
                else if (rObj instanceof List) {
                    List<?> l = (List<?>) rObj;
                    if (!l.isEmpty()) miRol = l.get(0).toString();
                }
                cargarNotificaciones();
            }
        });

        ImageButton btnBorrarTodo = findViewById(R.id.btnBorrarTodo);
        if (btnBorrarTodo != null) {
            btnBorrarTodo.setOnClickListener(v -> mostrarDialogoBorrarTodo());
        }
    }

    /**
     * Despliega cuadro de confirmación para la purga de alertas.
     */
    private void mostrarDialogoBorrarTodo() {
        new AlertDialog.Builder(this)
                .setTitle("Vaciar Bandeja 🗑️")
                .setMessage("¿Estás seguro de que deseas eliminar todas las notificaciones?")
                .setPositiveButton("Sí, borrar", (d, w) -> borrarTodasLasNotificaciones())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Ejecuta una operación atómica de eliminación masiva en Firestore.
     */
    private void borrarTodasLasNotificaciones() {
        String dest = ("VETERINARIO".equals(miRol) || "ADMIN".equals(miRol)) ? "CLINICA" : miUid;
        db.collection("notificaciones").whereEqualTo("uidDestinatario", dest).get().addOnSuccessListener(query -> {
            com.google.firebase.firestore.WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot doc : query) {
                batch.delete(doc.getReference());
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Bandeja vaciada", Toast.LENGTH_SHORT).show();
                Logger.log("Usuario " + miUid + " vació sus notificaciones.");
                cargarNotificaciones();
            });
        });
    }

    /**
     * Recupera del servidor los avisos correspondientes al perfil actual.
     */
    private void cargarNotificaciones() {
        if (contenedor == null) return;
        contenedor.removeAllViews();

        String dest = ("VETERINARIO".equals(miRol) || "ADMIN".equals(miRol)) ? "CLINICA" : miUid;

        db.collection("notificaciones")
                .whereEqualTo("uidDestinatario", dest)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            crearFilaNotificacion(doc.getId(), doc.getString("mensaje"), doc.getBoolean("leida"));
                        }
                    }
                });
    }

    /**
     * Construye y renderiza dinámicamente un aviso en la bandeja.
     */
    private void crearFilaNotificacion(String idDoc, String mensaje, boolean leida) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(35, 35, 35, 35);
        tarjeta.setBackgroundResource(R.drawable.bg_notif_item);
        if (!leida) tarjeta.setBackgroundColor(Color.parseColor("#E8F5E9"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 15);
        tarjeta.setLayoutParams(params);

        TextView tv = new TextView(this);
        tv.setText(mensaje);
        tv.setTextSize(15);
        tv.setTextColor(Color.BLACK);
        tarjeta.addView(tv);

        // Marcar como leída al interactuar
        tarjeta.setOnClickListener(v -> {
            db.collection("notificaciones").document(idDoc).update("leida", true).addOnSuccessListener(aVoid -> {
                tarjeta.setBackgroundColor(Color.WHITE);
            });
        });

        contenedor.addView(tarjeta);
    }
}