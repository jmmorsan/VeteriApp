package com.example.veteriapp.main;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.veteriapp.R;
import com.example.veteriapp.model.EstadoCita;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase AdminCitasActivity.
 * 
 * Interfaz de Gestión Clínica de la Agenda.
 * Centraliza la supervisión de solicitudes de consulta, permitiendo al
 * veterinario confirmar, rechazar o archivar las citas registradas.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class AdminCitasActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedorCitas;

    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseFirestore db;

    /**
     * Inicialización del gestor de agenda.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Activación de interfaz inmersiva
        android.view.View decorView = getWindow().getDecorView();
        int uiOptions = android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                      | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_admin_citas);

        db = FirebaseFirestore.getInstance();
        contenedorCitas = findViewById(R.id.contenedorCitas);

        // Configuración de Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAdminCitas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        cargarCitasPendientes();
    }

    /**
     * Recupera y ordena cronológicamente las consultas médicas registradas.
     */
    private void cargarCitasPendientes() {
        if (contenedorCitas == null) return;
        contenedorCitas.removeAllViews();

        db.collection("citas")
                .orderBy("fechaHora", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String idDoc = doc.getId();
                            
                            // Lectura de identificador robusto
                            Object idObj = doc.get("id_cita") != null ? doc.get("id_cita") : doc.get("id");
                            String idNumStr = (idObj != null) ? idObj.toString() : "---";
                            
                            String d = doc.getString("nombreUsuario");
                            String m = doc.getString("nombreMascota");
                            String t = doc.getString("tipo") != null ? doc.getString("tipo") : "General";
                            String mo = doc.getString("motivo");
                            String est = doc.getString("estado");
                            String uidD = doc.getString("uidUsuario");

                            Timestamp ts = doc.getTimestamp("fechaHora");
                            String f = (ts != null) ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts.toDate()) : "---";

                            crearTarjetaCita(idDoc, idNumStr, d, m, t, mo, f, est, uidD);
                        }
                    } else {
                        TextView tv = new TextView(this);
                        tv.setText("No hay citas en la agenda.");
                        contenedorCitas.addView(tv);
                    }
                });
    }

    /**
     * Construye dinámicamente la representación visual de cada cita en la agenda.
     */
    private void crearTarjetaCita(String idDoc, String idNum, String dueno, String mascota, String tipo, String motivo, String fecha, String estado, String uidD) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(30, 30, 30, 30);
        tarjeta.setBackgroundResource(R.drawable.bg_notif_item);
        
        // Identificación visual por estados
        if (EstadoCita.PENDIENTE.name().equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#FFF59D"));
        else if (EstadoCita.CONFIRMADA.name().equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#A5D6A7"));
        else if (EstadoCita.ARCHIVADA.name().equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#E0E0E0"));
        else tarjeta.setBackgroundColor(Color.parseColor("#EF9A9A"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        tarjeta.setLayoutParams(params);

        TextView tv = new TextView(this);
        tv.setText("📌 Cita #" + idNum + " [" + tipo + "]\n👤 " + dueno + " | 🐾 " + mascota + "\n📅 " + fecha + "\n📝 " + motivo);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        tarjeta.addView(tv);

        tarjeta.setOnClickListener(v -> mostrarMenuOpciones(idDoc, uidD, mascota, estado));
        contenedorCitas.addView(tarjeta);
    }

    /**
     * Despliega acciones administrativas según el estado de progresión de la cita.
     */
    private void mostrarMenuOpciones(String idDoc, String uidD, String mascota, String estadoActual) {
        String[] opciones;
        if (EstadoCita.PENDIENTE.name().equals(estadoActual)) {
            opciones = new String[]{"✅ Confirmar Cita", "❌ Rechazar Cita", "🗑️ Eliminar Registro"};
        } else if (EstadoCita.CONFIRMADA.name().equals(estadoActual)) {
            opciones = new String[]{"📂 Archivar Cita", "❌ Cancelar Cita", "🗑️ Eliminar Registro"};
        } else {
            opciones = new String[]{"♻️ Volver a Pendiente", "🗑️ Eliminar Registro"};
        }

        new AlertDialog.Builder(this)
                .setTitle("Gestión de Agenda")
                .setItems(opciones, (dialog, which) -> {
                    if (EstadoCita.PENDIENTE.name().equals(estadoActual)) {
                        if (which == 0) actualizarCitaEnFirebase(idDoc, EstadoCita.CONFIRMADA.name(), uidD, mascota);
                        else if (which == 1) actualizarCitaEnFirebase(idDoc, EstadoCita.RECHAZADA.name(), uidD, mascota);
                        else eliminarCita(idDoc);
                    } else if (EstadoCita.CONFIRMADA.name().equals(estadoActual)) {
                        if (which == 0) actualizarCitaEnFirebase(idDoc, EstadoCita.ARCHIVADA.name(), uidD, mascota);
                        else if (which == 1) actualizarCitaEnFirebase(idDoc, EstadoCita.RECHAZADA.name(), uidD, mascota);
                        else eliminarCita(idDoc);
                    } else {
                        if (which == 0) actualizarCitaEnFirebase(idDoc, EstadoCita.PENDIENTE.name(), uidD, mascota);
                        else eliminarCita(idDoc);
                    }
                }).show();
    }

    /**
     * Purga física del registro de cita en Firestore.
     */
    private void eliminarCita(String id) {
        db.collection("citas").document(id).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
            cargarCitasPendientes();
        });
    }

    /**
     * Sincroniza el cambio de estado en la nube y notifica al cliente final.
     */
    private void actualizarCitaEnFirebase(String idDoc, String nuevoEstado, String uidD, String mascota) {
        db.collection("citas").document(idDoc).update("estado", nuevoEstado).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Estado actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show();
            
            Map<String, Object> notif = new HashMap<>();
            notif.put("uidDestinatario", uidD);
            notif.put("mensaje", "Estado de cita para " + mascota + ": " + nuevoEstado.toLowerCase());
            notif.put("leida", false);
            notif.put("timestamp", Timestamp.now());
            db.collection("notificaciones").add(notif);

            cargarCitasPendientes();
        });
    }
}