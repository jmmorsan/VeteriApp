package com.example.veteriapp.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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

import com.example.veteriapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Clase MisMascotasActivity.
 * 
 * Interfaz de Gestión del Censo de Mascotas del Dueño.
 * Permite visualizar el listado de animales vinculados al usuario autenticado,
 * mostrando su estado administrativo y proporcionando acceso a la ficha detallada.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class MisMascotasActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedor;
    
    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseFirestore db;
    private String miUid;

    /**
     * Inicialización de la actividad y vinculación de componentes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configuración visual adaptativa
        android.view.View decorView = getWindow().getDecorView();
        int uiOptions = android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                      | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_mis_mascotas);

        db = FirebaseFirestore.getInstance();
        miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contenedor = findViewById(R.id.contenedorMisMascotas);

        // Configuración de Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMisMascotas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Evento de Alta
        FloatingActionButton fab = findViewById(R.id.fabAddMascota);
        if (fab != null) {
            fab.setOnClickListener(v -> startActivity(new Intent(this, AltaMascotaActivity.class)));
        }
    }

    /**
     * Recarga el listado de mascotas al retomar el foco de la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarMisMascotas();
    }

    /**
     * Recupera todas las mascotas asociadas al UID del dueño actual.
     */
    private void cargarMisMascotas() {
        if (contenedor == null) return;
        contenedor.removeAllViews();

        db.collection("mascotas")
                .whereEqualTo("uidDueno", miUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            
                            // Lectura de identificador secuencial
                            Object idObj = document.get("id_mascota") != null ? document.get("id_mascota") : document.get("id");
                            String idNumStr = (idObj != null) ? idObj.toString() : "---";

                            String nombre = document.getString("nombre");
                            String especie = document.getString("especie");
                            String raza = document.getString("raza");
                            String genero = document.getString("genero") != null ? document.getString("genero") : "---";
                            String estado = document.getString("estado");
                            String fotoBase64 = document.getString("fotoBase64");
                            String peso = (document.get("peso") != null) ? document.get("peso").toString() : "0";
                            String chip = (document.get("chip") != null) ? document.get("chip").toString() : "0";
                            
                            // Formateo de fecha Timestamp
                            String fechaStr = "---";
                            Object fObj = document.get("fechaNacimiento");
                            if (fObj instanceof Timestamp) {
                                fechaStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(((Timestamp) fObj).toDate());
                            } else if (fObj != null) {
                                fechaStr = fObj.toString();
                            }

                            crearTarjetaMascota(idNumStr, nombre, especie, raza, genero, estado, fotoBase64, fechaStr, peso, chip);
                        }
                    } else {
                        TextView tv = new TextView(this);
                        tv.setText("No tienes mascotas registradas.");
                        tv.setGravity(Gravity.CENTER);
                        tv.setPadding(0, 50, 0, 0);
                        contenedor.addView(tv);
                    }
                });
    }

    /**
     * Construye y renderiza la tarjeta visual para una mascota.
     */
    private void crearTarjetaMascota(String idNum, String nombre, String especie, String raza, String genero, String estado, 
                                     String fotoBase64, String fecha, String peso, String chip) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(30, 30, 30, 30);
        tarjeta.setGravity(Gravity.CENTER_HORIZONTAL);
        tarjeta.setBackgroundResource(R.drawable.bg_notif_item);
        
        // Identificación visual por colores
        if ("ACEPTADA".equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#C8E6C9"));
        else if ("RECHAZADA".equals(estado)) tarjeta.setBackgroundColor(Color.parseColor("#FFCDD2"));
        else tarjeta.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        tarjeta.setLayoutParams(params);

        // Decodificación multimedia Base64
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView img = new ImageView(this);
                img.setImageBitmap(bitmap);
                img.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tarjeta.addView(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        TextView datos = new TextView(this);
        datos.setText("🐾 [" + idNum + "] " + nombre + " (" + genero + ")\n" + especie + " - " + raza + "\nEstado: " + estado);
        datos.setTextSize(18);
        datos.setGravity(Gravity.CENTER);
        datos.setTextColor(Color.BLACK);
        tarjeta.addView(datos);

        // Navegación al Detalle
        tarjeta.setOnClickListener(v -> {
            Intent i = new Intent(MisMascotasActivity.this, DetalleMascotaActivity.class);
            i.putExtra("nombre", nombre);
            i.putExtra("especie", especie);
            i.putExtra("raza", raza);
            i.putExtra("genero", genero);
            i.putExtra("estado", estado);
            i.putExtra("foto", fotoBase64);
            i.putExtra("fecha", fecha);
            i.putExtra("peso", peso);
            i.putExtra("chip", chip);
            startActivity(i);
        });

        contenedor.addView(tarjeta);
    }
}