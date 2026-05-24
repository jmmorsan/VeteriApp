package com.example.veteriapp.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.veteriapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Clase BandejaChatActivity.
 * 
 * Interfaz de Multichat para el Veterinario.
 * Permite visualizar el listado de clientes que han iniciado una consulta,
 * mostrando alertas visuales para mensajes no leídos y proporcionando acceso
 * directo a las salas de chat individuales.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class BandejaChatActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedorClientes;
    
    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseFirestore db;

    /**
     * Inicializa la bandeja de entrada clínica.
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

        setContentView(R.layout.activity_bandeja_chat);

        db = FirebaseFirestore.getInstance();
        contenedorClientes = findViewById(R.id.contenedorClientes);

        // Configuración de Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarBandeja);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        cargarListaClientes();
    }

    /**
     * Recupera del servidor los perfiles de usuario que poseen el rol de Dueño.
     */
    private void cargarListaClientes() {
        if (contenedorClientes == null) return;
        contenedorClientes.removeAllViews();

        db.collection("users").whereEqualTo("rol", "DUEÑO").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String uid = doc.getString("uid");
                    String nombre = doc.getString("nombre");
                    String email = doc.getString("email");
                    
                    if (uid != null) {
                        crearTarjetaCliente(uid, nombre, email);
                    }
                }
            }
        });
    }

    /**
     * Crea un elemento visual de cliente con contador de notificaciones integrado.
     */
    private void crearTarjetaCliente(String uid, String nombre, String email) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.HORIZONTAL);
        tarjeta.setPadding(40, 40, 40, 40);
        tarjeta.setGravity(Gravity.CENTER_VERTICAL);
        tarjeta.setBackgroundResource(R.drawable.bg_notif_item);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        tarjeta.setLayoutParams(params);

        // Bloque de información del cliente
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvN = new TextView(this);
        tvN.setText(nombre);
        tvN.setTextSize(18);
        tvN.setTextColor(Color.BLACK);
        tvN.setTypeface(null, Typeface.BOLD);
        info.addView(tvN);

        TextView tvE = new TextView(this);
        tvE.setText(email);
        tvE.setTextSize(14);
        info.addView(tvE);

        tarjeta.addView(info);

        // Indicador de mensajes pendientes (Badge)
        View punto = new View(this);
        punto.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
        punto.setBackgroundResource(R.drawable.bg_punto_rojo);
        punto.setVisibility(View.GONE);
        tarjeta.addView(punto);

        // Escuchador de mensajes no leídos específicos
        db.collection("notificaciones")
                .whereEqualTo("uidDestinatario", "CLINICA")
                .whereEqualTo("leida", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null && !value.isEmpty()) {
                        punto.setVisibility(View.VISIBLE);
                    } else {
                        punto.setVisibility(View.GONE);
                    }
                });

        // Evento de apertura de sala de chat
        tarjeta.setOnClickListener(v -> {
            Intent i = new Intent(BandejaChatActivity.this, ChatActivity.class);
            i.putExtra("uidCliente", uid);
            startActivity(i);
        });

        contenedorClientes.addView(tarjeta);
    }
}