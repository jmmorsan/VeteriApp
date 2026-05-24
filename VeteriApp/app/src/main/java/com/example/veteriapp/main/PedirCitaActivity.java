package com.example.veteriapp.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.veteriapp.R;
import com.example.veteriapp.model.EstadoCita;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase PedirCitaActivity.
 * 
 * Gestiona la solicitud de consultas médicas por parte del dueño.
 * Implementa el filtrado dinámico de mascotas (solo en estado ACEPTADA),
 * la selección de tipo de servicio clínico y la persistencia secuencial.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class PedirCitaActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private Spinner spMascota, spTipo;
    private List<String> listaNombresMascotas;
    private ArrayAdapter<String> adapterMascotas;
    private EditText etFecha, etMotivo;
    private Button btnEnviar;
    private LinearLayout contenedorMisCitas;

    // --- INSTANCIAS DE FIREBASE Y ESTADO ---
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Date fechaHoraElegida;

    /**
     * Inicializa los componentes de solicitud de cita.
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

        setContentView(R.layout.activity_pedir_cita);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Configuración de Barra de Herramientas
        Toolbar toolbar = findViewById(R.id.toolbarCita);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Vinculación de Componentes
        spMascota = findViewById(R.id.spMascotaCita);
        spTipo = findViewById(R.id.spTipoCita);
        etFecha = findViewById(R.id.etFechaCita);
        etMotivo = findViewById(R.id.etMotivoCita);
        btnEnviar = findViewById(R.id.btnSolicitarCita);
        contenedorMisCitas = findViewById(R.id.contenedorMisCitas);

        // Configuración de Spinner de Mascotas
        listaNombresMascotas = new ArrayList<>();
        adapterMascotas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaNombresMascotas);
        adapterMascotas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMascota.setAdapter(adapterMascotas);

        // Configuración de Spinner de Tipos de Cita
        String[] tipos = {"Cita General 🩺", "Vacunación 💉", "Peluquería ✂️"};
        ArrayAdapter<String> adapterTipos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapterTipos);

        // Carga de Datos Inicial
        cargarMisMascotasEnSpinner();
        etFecha.setOnClickListener(v -> mostrarSelectorFechaHora());
        cargarMisCitas();
        btnEnviar.setOnClickListener(v -> guardarCita());
    }

    /**
     * Recupera del servidor únicamente las mascotas aptas (estado ACEPTADA).
     */
    private void cargarMisMascotasEnSpinner() {
        String miUid = mAuth.getCurrentUser().getUid();
        db.collection("mascotas")
                .whereEqualTo("uidDueno", miUid)
                .whereEqualTo("estado", "ACEPTADA")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaNombresMascotas.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            listaNombresMascotas.add(document.getString("nombre"));
                        }
                        if (listaNombresMascotas.isEmpty()) {
                            listaNombresMascotas.add("No tienes mascotas aptas");
                            btnEnviar.setEnabled(false);
                        } else {
                            btnEnviar.setEnabled(true);
                        }
                        adapterMascotas.notifyDataSetChanged();
                    }
                });
    }

    /**
     * Despliega selectores nativos de fecha y hora encadenados.
     */
    private void mostrarSelectorFechaHora() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day);
            new TimePickerDialog(this, (view1, hour, minute) -> {
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                fechaHoraElegida = cal.getTime();
                etFecha.setText(day + "/" + (month + 1) + "/" + year + " " + String.format("%02d:%02d", hour, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Procesa y persiste la solicitud de cita con identificador incremental.
     */
    private void guardarCita() {
        String mascota = spMascota.getSelectedItem() != null ? spMascota.getSelectedItem().toString() : "";
        String tipo = spTipo.getSelectedItem().toString();
        String motivo = etMotivo.getText().toString().trim();

        if (mascota.contains("No tienes") || fechaHoraElegida == null) {
            Toast.makeText(this, "Selecciona una mascota válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                final String nombreReal = doc.getString("nombre");
                db.collection("citas").orderBy("id_cita", Query.Direction.DESCENDING).limit(1).get()
                        .addOnSuccessListener(snap -> {
                            int idC = 1;
                            if (!snap.isEmpty()) {
                                Number ult = snap.getDocuments().get(0).getLong("id_cita");
                                if (ult != null) idC = ult.intValue() + 1;
                            }
                            
                            Map<String, Object> data = new HashMap<>();
                            data.put("id_cita", idC);
                            data.put("uidUsuario", uid);
                            data.put("nombreUsuario", nombreReal);
                            data.put("nombreMascota", mascota);
                            data.put("tipo", tipo);
                            data.put("motivo", motivo);
                            data.put("fechaHora", new Timestamp(fechaHoraElegida));
                            data.put("estado", EstadoCita.PENDIENTE.name());

                            db.collection("citas").add(data).addOnSuccessListener(ref -> {
                                emitirNotificacionClinica(nombreReal, mascota);
                                finish();
                            });
                        });
            }
        });
    }

    /**
     * Registra un aviso de nueva cita para el personal clínico.
     */
    private void emitirNotificacionClinica(String dueno, String mascota) {
        db.collection("notificaciones").orderBy("id_notificacion", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(snap -> {
                    int idN = 1;
                    if (!snap.isEmpty()) {
                        Number ult = snap.getDocuments().get(0).getLong("id_notificacion");
                        if (ult != null) idN = ult.intValue() + 1;
                    }
                    Map<String, Object> n = new HashMap<>();
                    n.put("id_notificacion", idN);
                    n.put("uidDestinatario", "CLINICA");
                    n.put("mensaje", "Nueva cita: " + dueno + " para " + mascota);
                    n.put("leida", false);
                    n.put("timestamp", Timestamp.now());
                    db.collection("notificaciones").add(n);
                });
    }

    /**
     * Recupera el historial de solicitudes propias del usuario.
     */
    private void cargarMisCitas() {
        if (contenedorMisCitas == null) return;
        contenedorMisCitas.removeAllViews();
        db.collection("citas").whereEqualTo("uidUsuario", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String t = doc.getString("nombreMascota");
                    String s = doc.getString("tipo") != null ? doc.getString("tipo") : "General";
                    String st = doc.getString("estado");
                    
                    Timestamp ts = doc.getTimestamp("fechaHora");
                    String f = (ts != null) ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts.toDate()) : "---";

                    crearTarjetaVisual(doc.getId(), t, s, f, st);
                }
            }
        });
    }

    /**
     * Construye el elemento visual para cada cita en el historial.
     */
    private void crearTarjetaVisual(String idDoc, String mascota, String tipo, String fecha, String estado) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        layout.setBackgroundResource(R.drawable.bg_notif_item);

        if (EstadoCita.PENDIENTE.name().equals(estado)) layout.setBackgroundColor(Color.parseColor("#FFF59D"));
        else if (EstadoCita.CONFIRMADA.name().equals(estado)) layout.setBackgroundColor(Color.parseColor("#A5D6A7"));
        else if (EstadoCita.ARCHIVADA.name().equals(estado)) layout.setBackgroundColor(Color.parseColor("#E0E0E0"));
        else layout.setBackgroundColor(Color.parseColor("#EF9A9A"));

        TextView tv = new TextView(this);
        tv.setText("🐾 " + mascota + " (" + tipo + ")\n📅 " + fecha + "\nEstado: " + estado);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        layout.addView(tv);

        if (!EstadoCita.PENDIENTE.name().equals(estado)) {
            Button btn = new Button(this);
            btn.setText("Borrar");
            btn.setOnClickListener(v -> db.collection("citas").document(idDoc).delete().addOnSuccessListener(aVoid -> cargarMisCitas()));
            layout.addView(btn);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        layout.setLayoutParams(params);
        contenedorMisCitas.addView(layout);
    }
}