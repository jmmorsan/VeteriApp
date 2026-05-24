package com.example.veteriapp.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.veteriapp.R;
import com.example.veteriapp.model.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase ChatActivity.
 * 
 * Gestiona la comunicación en tiempo real entre el cliente y el personal médico.
 * Implementa el redimensionado dinámico de teclado, scroll automático al recibir 
 * mensajes y emisión de notificaciones de chat.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class ChatActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedorMensajes;
    private ScrollView scrollViewChat;
    private EditText etMensaje;
    private Button btnEnviar;

    // --- INSTANCIAS DE FIREBASE Y ESTADO ---
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String idSalaChat;
    private String miUid;

    /**
     * Inicialización del entorno de chat bidireccional.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // --- CONFIGURACIÓN DE PANTALLA ADAPTATIVA ---
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_chat);

        // --- INICIALIZACIÓN DE SERVICIOS ---
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        miUid = mAuth.getCurrentUser().getUid();

        contenedorMensajes = findViewById(R.id.contenedorMensajes);
        scrollViewChat = findViewById(R.id.scrollViewChat);
        etMensaje = findViewById(R.id.etMensajeChat);
        btnEnviar = findViewById(R.id.btnEnviarMensaje);

        // Determinación de la sala (ID único por cliente)
        String uidClienteIntent = getIntent().getStringExtra("uidCliente");
        if (uidClienteIntent != null) {
            idSalaChat = uidClienteIntent;
        } else {
            idSalaChat = miUid;
        }

        escucharMensajes();
        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    /**
     * Activa el SnapshotListener para monitorizar la entrada de mensajes en tiempo real.
     */
    private void escucharMensajes() {
        db.collection("mensajes")
                .whereEqualTo("idSala", idSalaChat)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        contenedorMensajes.removeAllViews();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            Mensaje m = doc.toObject(Mensaje.class);
                            pintarBurbuja(m);
                        }
                        // Desplazamiento automático al último mensaje
                        scrollViewChat.post(() -> scrollViewChat.fullScroll(View.FOCUS_DOWN));
                    }
                });
    }

    /**
     * Registra un nuevo mensaje en Firestore y lanza la alerta correspondiente.
     */
    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        db.collection("mensajes").orderBy("id_mensaje", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(snap -> {
                    int idCalc = 1;
                    if (!snap.isEmpty()) {
                        Number ult = snap.getDocuments().get(0).getLong("id_mensaje");
                        if (ult != null) idCalc = ult.intValue() + 1;
                    }
                    
                    final int idFinal = idCalc;
                    Map<String, Object> data = new HashMap<>();
                    data.put("id_mensaje", idFinal);
                    data.put("uidRemitente", miUid);
                    data.put("texto", texto);
                    data.put("idSala", idSalaChat);
                    data.put("timestamp", com.google.firebase.Timestamp.now());

                    db.collection("mensajes").add(data).addOnSuccessListener(ref -> {
                        String uidDestino = idSalaChat.equals(miUid) ? "CLINICA" : idSalaChat;
                        emitirNotificacion(uidDestino, texto);
                        etMensaje.setText("");
                        scrollViewChat.post(() -> scrollViewChat.fullScroll(View.FOCUS_DOWN));
                    });
                });
    }

    /**
     * Genera una notificación asíncrona para alertar al destinatario del nuevo mensaje.
     * 
     * @param uidDestino Identificador del receptor o identificador global de clínica.
     * @param texto      Cuerpo del mensaje recibido.
     */
    private void emitirNotificacion(String uidDestino, String texto) {
        db.collection("notificaciones").orderBy("id_notificacion", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(snap -> {
                    int idN = 1;
                    if (!snap.isEmpty()) {
                        Number ult = snap.getDocuments().get(0).getLong("id_notificacion");
                        if (ult != null) idN = ult.intValue() + 1;
                    }
                    
                    Map<String, Object> notif = new HashMap<>();
                    notif.put("id_notificacion", idN);
                    notif.put("uidDestinatario", uidDestino);
                    notif.put("mensaje", "Nuevo mensaje: " + texto);
                    notif.put("leida", false);
                    notif.put("timestamp", com.google.firebase.Timestamp.now());

                    db.collection("notificaciones").add(notif);
                });
    }

    /**
     * Renderiza dinámicamente un mensaje en la interfaz con alineación según el remitente.
     * 
     * @param m Objeto mensaje a visualizar.
     */
    private void pintarBurbuja(Mensaje m) {
        TextView tv = new TextView(this);
        tv.setText(m.getTexto());
        tv.setPadding(35, 25, 35, 25);
        tv.setTextSize(16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 20, 10);

        if (m.getUidRemitente().equals(miUid)) {
            tv.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
            params.gravity = Gravity.END;
            tv.setTextColor(Color.BLACK);
        } else {
            tv.setBackgroundResource(android.R.drawable.dialog_holo_dark_frame);
            params.gravity = Gravity.START;
            tv.setTextColor(Color.WHITE);
        }

        tv.setLayoutParams(params);
        contenedorMensajes.addView(tv);
    }
}