package com.example.veteriapp.main;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.veteriapp.R;
import com.example.veteriapp.model.GeneroMascota;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase AltaMascotaActivity.
 * 
 * Gestiona el proceso de registro de nuevos pacientes en el sistema.
 * Implementa la captura de imagen desde galería, serialización Base64,
 * y persistencia robusta en Firestore con IDs secuenciales automáticos.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class AltaMascotaActivity extends AppCompatActivity {

    // --- VARIABLES DE LA INTERFAZ ---
    private ImageView imgMascota;
    private EditText etNombre, etEspecie, etRaza, etFecha, etPeso, etChip;
    private Spinner spGenero;
    private Button btnGuardar;

    // --- GESTIÓN DE MULTIMEDIA Y ESTADO ---
    private Uri uriImagenSeleccionada;
    private Bitmap bitmapImagenOriginal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Date fechaNacimientoElegida;

    /**
     * Inicialización de la actividad de alta.
     * Configura el entorno visual inmersivo y vincula los componentes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Activación del Modo Inmersivo Total (Ajuste de pantalla S25+)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_alta_mascota);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configuración de la Barra de Herramientas
        Toolbar toolbar = findViewById(R.id.toolbarAlta);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Vinculación de Componentes
        imgMascota = findViewById(R.id.imgMascotaAlta);
        etNombre = findViewById(R.id.etNombreMascota);
        etEspecie = findViewById(R.id.etEspecieMascota);
        etRaza = findViewById(R.id.etRazaMascota);
        etFecha = findViewById(R.id.etFechaMascota);
        etPeso = findViewById(R.id.etPesoMascota);
        etChip = findViewById(R.id.etChipMascota);
        spGenero = findViewById(R.id.spGeneroMascota);
        btnGuardar = findViewById(R.id.btnGuardarMascota);

        // Poblado del Spinner de Género (Enum)
        ArrayAdapter<GeneroMascota> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, GeneroMascota.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenero.setAdapter(adapter);

        // Asignación de Eventos
        findViewById(R.id.cardFotoAlta).setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, 100);
        });

        etFecha.setOnClickListener(v -> mostrarCalendario());
        btnGuardar.setOnClickListener(v -> procesarYGuardar());
    }

    /**
     * Despliega un selector de fecha nativo para registrar la fecha de nacimiento.
     */
    private void mostrarCalendario() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day, 0, 0, 0);
            fechaNacimientoElegida = cal.getTime();
            etFecha.setText(day + "/" + (month + 1) + "/" + year);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Captura el resultado de la selección de imagen multimedia.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
            uriImagenSeleccionada = data.getData();
            try {
                InputStream is = getContentResolver().openInputStream(uriImagenSeleccionada);
                bitmapImagenOriginal = BitmapFactory.decodeStream(is);
                imgMascota.setImageBitmap(bitmapImagenOriginal);
                imgMascota.setPadding(0, 0, 0, 0);
                imgMascota.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Valida los campos y calcula el siguiente ID secuencial disponible en Firestore.
     */
    private void procesarYGuardar() {
        String nombre = etNombre.getText().toString().trim();
        String especie = etEspecie.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String peso = etPeso.getText().toString().trim();
        String chip = etChip.getText().toString().trim();
        GeneroMascota genero = (GeneroMascota) spGenero.getSelectedItem();

        if (nombre.isEmpty() || especie.isEmpty() || fechaNacimientoElegida == null) {
            Toast.makeText(this, "Datos obligatorios incompletos", Toast.LENGTH_SHORT).show();
            return;
        }

        final String fotoBase64 = (bitmapImagenOriginal != null) ? codificarImagenABase64(bitmapImagenOriginal) : "";

        // --- LÓGICA DE ID_MASCOTA SECUENCIAL ---
        db.collection("mascotas").orderBy("id_mascota", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(snap -> {
                    int idCalc = 1;
                    if (!snap.isEmpty()) {
                        Number ultimoId = snap.getDocuments().get(0).getLong("id_mascota");
                        if (ultimoId != null) idCalc = ultimoId.intValue() + 1;
                    }
                    guardarEnFirestore(idCalc, nombre, especie, raza, peso, chip, fotoBase64, genero.name());
                });
    }

    /**
     * Serializa un Bitmap en una cadena Base64 optimizada.
     * 
     * @param bmp Imagen original.
     * @return Cadena serializada en Base64.
     */
    private String codificarImagenABase64(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compresión al 60% para optimizar el almacenamiento sin perder detalle clínico
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Persiste el objeto de mascota definitivo en Firebase Firestore.
     */
    private void guardarEnFirestore(int idNum, String n, String e, String r, String p, String c, String img, String gen) {
        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> mascota = new HashMap<>();
        
        mascota.put("id_mascota", idNum);
        mascota.put("id", idNum); // Compatibilidad
        mascota.put("nombre", n);
        mascota.put("especie", e);
        mascota.put("raza", r);
        mascota.put("genero", gen);
        mascota.put("fechaNacimiento", new Timestamp(fechaNacimientoElegida));
        mascota.put("peso", p);
        mascota.put("chip", c);
        mascota.put("fotoBase64", img);
        mascota.put("uidDueno", uid);
        mascota.put("estado", "PENDIENTE");
        mascota.put("timestamp", Timestamp.now());

        db.collection("mascotas").add(mascota).addOnSuccessListener(doc -> {
            Toast.makeText(this, "Paciente #" + idNum + " registrado con éxito", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}