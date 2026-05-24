package com.example.veteriapp.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.veteriapp.R;
import com.example.veteriapp.utils.Logger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase AdminMascotasActivity.
 * 
 * Interfaz de Supervisión Clínica de Pacientes.
 * Permite al personal médico auditar las solicitudes de alta de mascotas,
 * validar su estado de salud (Aceptar/Rechazar) y gestionar el traslado al Memorial.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class AdminMascotasActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedorPacientes;
    
    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseFirestore db;

    /**
     * Inicializa la actividad de revisión de pacientes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configuración de pantalla adaptativa
        android.view.View decorView = getWindow().getDecorView();
        int uiOptions = android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                      | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_admin_mascotas);

        db = FirebaseFirestore.getInstance();
        contenedorPacientes = findViewById(R.id.contenedorPacientes);

        // Configuración de Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAdminMascotas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        cargarPacientes();
    }

    /**
     * Recupera el censo médico completo sin filtros de ordenación conflictivos.
     */
    private void cargarPacientes() {
        if (contenedorPacientes == null) return;
        contenedorPacientes.removeAllViews();

        db.collection("mascotas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String idDoc = document.getId();
                    
                    Object idObj = document.get("id_mascota") != null ? document.get("id_mascota") : document.get("id");
                    String idNumStr = (idObj != null) ? idObj.toString() : "---";
                    
                    String nombre = document.getString("nombre");
                    String especie = document.getString("especie");
                    String raza = document.getString("raza");
                    String genero = document.getString("genero") != null ? document.getString("genero") : "---";
                    String estado = document.getString("estado");
                    String fotoBase64 = document.getString("fotoBase64");
                    
                    Object udObj = document.get("uidDueno");
                    String udStr = (udObj != null) ? udObj.toString() : "";

                    crearTarjetaMascota(idDoc, idNumStr, nombre, especie, raza, genero, estado, fotoBase64, udStr);
                }
            }
        });
    }

    /**
     * Construye la ficha visual de cada paciente en el listado médico.
     */
    private void crearTarjetaMascota(String idDoc, String idNum, String nombre, String especie, String raza, String genero, String estado, String fotoBase64, String uidDueno) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(30, 30, 30, 30);
        tarjeta.setGravity(Gravity.CENTER_HORIZONTAL);
        tarjeta.setBackgroundResource(R.drawable.bg_notif_item);

        // Semáforo de estados clínicos
        if ("PENDIENTE".equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#FFF59D"));
        else if ("MEMORIAL".equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#E0E0E0"));
        else if ("RECHAZADA".equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#FFCDD2"));
        else tarjeta.setBackgroundColor(Color.WHITE);

        // Procesamiento multimedia Base64
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView foto = new ImageView(this);
                foto.setImageBitmap(bitmap);
                foto.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                foto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tarjeta.addView(foto);
            } catch (Exception e) { e.printStackTrace(); }
        }

        TextView datos = new TextView(this);
        datos.setText("🐾 Paciente #" + idNum + " | " + nombre + " (" + genero + ")\n" + especie + " (" + raza + ")\nEstado: " + estado);
        datos.setTextSize(16);
        datos.setGravity(Gravity.CENTER);
        tarjeta.addView(datos);

        tarjeta.setOnClickListener(v -> mostrarOpcionesVeterinario(idDoc, nombre, estado, uidDueno));
        contenedorPacientes.addView(tarjeta);
    }

    /**
     * Despliega el menú de acciones clínicas dinámicas según el estado del paciente.
     */
    private void mostrarOpcionesVeterinario(String idDoc, String nombre, String estadoActual, String uidDueno) {
        String[] opciones;
        if ("PENDIENTE".equals(estadoActual)) opciones = new String[]{"✅ Aprobar Ingreso", "❌ Rechazar Solicitud", "🗑️ Eliminar Registro"};
        else if ("RECHAZADA".equals(estadoActual)) opciones = new String[]{"🗑️ Eliminar Permanentemente", "♻️ Restaurar Solicitud"};
        else if ("MEMORIAL".equals(estadoActual)) opciones = new String[]{"🗑️ Depurar Archivo Histórico"};
        else opciones = new String[]{"🕊️ Trasladar a Memorial", "🗑️ Eliminar Registro"};

        new AlertDialog.Builder(this)
                .setTitle("Gestión Clínica: " + nombre)
                .setItems(opciones, (dialog, which) -> {
                    if ("PENDIENTE".equals(estadoActual)) {
                        if (which == 0) actualizarEstado(idDoc, "ACEPTADA", uidDueno, nombre);
                        else if (which == 1) actualizarEstado(idDoc, "RECHAZADA", uidDueno, nombre);
                        else eliminarRegistro(idDoc);
                    } else if ("RECHAZADA".equals(estadoActual)) {
                        if (which == 0) eliminarRegistro(idDoc);
                        else actualizarEstado(idDoc, "PENDIENTE", uidDueno, nombre);
                    } else if ("MEMORIAL".equals(estadoActual)) {
                        if (which == 0) eliminarRegistro(idDoc);
                    } else {
                        if (which == 0) actualizarEstado(idDoc, "MEMORIAL", uidDueno, nombre);
                        else eliminarRegistro(idDoc);
                    }
                }).show();
    }

    /**
     * Elimina físicamente el documento de la base de datos.
     */
    private void eliminarRegistro(String id) {
        db.collection("mascotas").document(id).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Registro depurado", Toast.LENGTH_SHORT).show();
            cargarPacientes();
        });
    }

    /**
     * Actualiza el estado administrativo del paciente y notifica al dueño.
     */
    private void actualizarEstado(String idDoc, String nuevoEstado, String uidDueno, String nombreMascota) {
        db.collection("mascotas").document(idDoc).update("estado", nuevoEstado).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Estado Sincronizado", Toast.LENGTH_SHORT).show();
            if (uidDueno != null && !uidDueno.isEmpty()) {
                Map<String, Object> notif = new HashMap<>();
                notif.put("uidDestinatario", uidDueno);
                notif.put("mensaje", "Informe Clínico: " + nombreMascota + " ahora está " + nuevoEstado.toLowerCase());
                notif.put("leida", false);
                notif.put("timestamp", com.google.firebase.Timestamp.now());
                db.collection("notificaciones").add(notif);
            }
            cargarPacientes();
        });
    }
}