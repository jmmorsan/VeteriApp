package com.example.veteriapp.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.veteriapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

/**
 * Clase MemorialActivity.
 * 
 * Memorial "El Puente del Arcoíris".
 * Espacio conmemorativo para el recuerdo de mascotas fallecidas.
 * Implementa filtros de privacidad para visualizar recuerdos propios o 
 * el muro público global de la clínica.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class MemorialActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedor;
    private Button btnPrivado, btnPublico;

    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseFirestore db;
    private String miUid;

    /**
     * Inicializa la actividad del memorial con modo inmersivo.
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

        setContentView(R.layout.activity_memorial);

        // Vinculación de Componentes
        db = FirebaseFirestore.getInstance();
        miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contenedor = findViewById(R.id.contenedorMemorial);
        btnPrivado = findViewById(R.id.btnMemorialPrivado);
        btnPublico = findViewById(R.id.btnMemorialPublico);

        // Configuración de Barra de Herramientas
        Toolbar toolbar = findViewById(R.id.toolbarMemorial);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        // LÓGICA DE COMPATIBILIDAD DE ROL (Filtrado de opciones)
        db.collection("users").document(miUid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String rolStr = "";
                Object rolObj = doc.get("rol");
                if (rolObj instanceof String) rolStr = (String) rolObj;
                else if (rolObj instanceof List) {
                    List<?> l = (List<?>) rolObj;
                    if (!l.isEmpty()) rolStr = l.get(0).toString();
                }

                if ("VETERINARIO".equals(rolStr) || "ADMIN".equals(rolStr)) {
                    if (btnPrivado != null) btnPrivado.setVisibility(View.GONE);
                    cargarMemorial(true);
                } else {
                    cargarMemorial(true);
                    btnPrivado.setOnClickListener(v -> cargarMemorial(false));
                    btnPublico.setOnClickListener(v -> cargarMemorial(true));
                }
            }
        }).addOnFailureListener(e -> cargarMemorial(true));
    }

    /**
     * Recupera los registros de mascotas en estado MEMORIAL.
     * 
     * @param esPublico true para visualizar el muro global, false para el personal.
     */
    private void cargarMemorial(boolean esPublico) {
        if (contenedor == null) return;
        contenedor.removeAllViews();

        db.collection("mascotas").whereEqualTo("estado", "MEMORIAL").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String idDoc = document.getId();
                    String nombre = document.getString("nombre");
                    String foto = document.getString("fotoBase64");
                    String dedicatoria = document.getString("dedicatoria");
                    String uidDuenoDoc = document.getString("uidDueno");

                    boolean esMia = miUid.equals(uidDuenoDoc);

                    if (esPublico || esMia) {
                        crearTarjetaHomenaje(idDoc, nombre, foto, dedicatoria, esMia);
                    }
                }
            }
        });
    }

    /**
     * Construye dinámicamente la tarjeta de recuerdo.
     */
    private void crearTarjetaHomenaje(String id, String nombre, String fotoBase64, String dedicatoria, boolean puedeEditar) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(40, 40, 40, 40);
        tarjeta.setGravity(Gravity.CENTER_HORIZONTAL);
        tarjeta.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 30);
        tarjeta.setLayoutParams(params);

        // Renderizado de Fotografía
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            try {
                byte[] bytes = android.util.Base64.decode(fotoBase64, android.util.Base64.DEFAULT);
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView img = new ImageView(this);
                img.setImageBitmap(bitmap);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, 400);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                img.setLayoutParams(lp);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tarjeta.addView(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        TextView tvNombre = new TextView(this);
        tvNombre.setText(nombre);
        tvNombre.setTextSize(24);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        tvNombre.setTextColor(Color.parseColor("#5D4037"));
        tvNombre.setGravity(Gravity.CENTER_HORIZONTAL);
        tarjeta.addView(tvNombre);

        TextView tvDedicatoria = new TextView(this);
        tvDedicatoria.setText(dedicatoria != null ? "\"" + dedicatoria + "\"" : "\"Siempre en nuestros corazones\"");
        tvDedicatoria.setTextSize(16);
        tvDedicatoria.setTypeface(null, android.graphics.Typeface.ITALIC);
        tvDedicatoria.setGravity(Gravity.CENTER);
        tvDedicatoria.setPadding(0, 10, 0, 10);
        tarjeta.addView(tvDedicatoria);

        if (puedeEditar) {
            Button btnEditar = new Button(this);
            btnEditar.setText("Escribir Dedicatoria");
            btnEditar.setOnClickListener(v -> mostrarDialogoDedicatoria(id, dedicatoria));
            tarjeta.addView(btnEditar);
        }

        contenedor.addView(tarjeta);
    }

    /**
     * Muestra el diálogo para que el dueño actualice su mensaje de recuerdo.
     */
    private void mostrarDialogoDedicatoria(String idMascota, String dedicatoriaActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Palabras de Recuerdo");
        final EditText input = new EditText(this);
        input.setText(dedicatoriaActual);
        builder.setView(input);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            db.collection("mascotas").document(idMascota).update("dedicatoria", input.getText().toString())
                    .addOnSuccessListener(aVoid -> cargarMemorial(false));
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}