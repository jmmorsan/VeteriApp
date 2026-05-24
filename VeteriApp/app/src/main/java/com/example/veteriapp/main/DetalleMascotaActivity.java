package com.example.veteriapp.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.veteriapp.R;

/**
 * Clase DetalleMascotaActivity.
 * 
 * Interfaz de Visualización Pormenorizada del Paciente.
 * Recupera la información clínica transmitida mediante Intent y procesa
 * la deserialización multimedia Base64 para reconstruir la fotografía.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class DetalleMascotaActivity extends AppCompatActivity {

    /**
     * Inicializa la ficha técnica del paciente.
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

        setContentView(R.layout.activity_detalle_mascota);

        // Configuración de Toolbar de navegación
        Toolbar toolbar = findViewById(R.id.toolbarDetalle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Vinculación de Componentes
        ImageView img = findViewById(R.id.imgDetalleMascota);
        TextView tvNombre = findViewById(R.id.tvNombreDetalle);
        TextView tvSub = findViewById(R.id.tvEspecieRazaDetalle);
        TextView tvFecha = findViewById(R.id.tvFechaDetalle);
        TextView tvPeso = findViewById(R.id.tvPesoDetalle);
        TextView tvChip = findViewById(R.id.tvChipDetalle);
        TextView tvEstado = findViewById(R.id.tvEstadoDetalle);

        // RECUPERACIÓN ROBUSTA DE DATOS (INTENT EXTRAS)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tvNombre.setText(obtenerExtraSeguro(extras, "nombre"));
            
            String genero = obtenerExtraSeguro(extras, "genero");
            tvSub.setText(obtenerExtraSeguro(extras, "especie") + " - " + obtenerExtraSeguro(extras, "raza") + " (" + genero + ")");
            
            tvFecha.setText("📅 Fecha Nacimiento: " + obtenerExtraSeguro(extras, "fecha"));
            tvPeso.setText("⚖️ Peso Actual: " + obtenerExtraSeguro(extras, "peso") + " Kg");
            tvChip.setText("🆔 Microchip: " + obtenerExtraSeguro(extras, "chip"));
            tvEstado.setText("📌 Estado: " + obtenerExtraSeguro(extras, "estado"));

            // Reconstrucción del recurso multimedia
            String fotoBase64 = obtenerExtraSeguro(extras, "foto");
            if (!fotoBase64.isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    img.setImageBitmap(bitmap);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Extrae de forma segura un valor del bundle previniendo nulidades.
     * 
     * @param b   Bundle de datos.
     * @param key Identificador de la clave.
     * @return Cadena de texto resultante.
     */
    private String obtenerExtraSeguro(Bundle b, String key) {
        Object val = b.get(key);
        return (val != null) ? val.toString() : "";
    }
}