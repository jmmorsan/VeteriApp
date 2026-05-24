package com.example.veteriapp.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.veteriapp.R;
import com.example.veteriapp.utils.Logger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase GestionRolesActivity.
 * 
 * Interfaz de Administración de Privilegios de Acceso.
 * Permite al administrador del sistema listar todos los perfiles registrados 
 * y reasignar roles dinámicamente entre Dueño, Veterinario y Administrador.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class GestionRolesActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private LinearLayout contenedor;
    
    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseFirestore db;

    /**
     * Inicializa la actividad de gestión de roles.
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

        setContentView(R.layout.activity_gestion_roles);

        db = FirebaseFirestore.getInstance();
        contenedor = findViewById(R.id.contenedorUsuariosRoles);

        // Configuración de Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarRoles);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        cargarUsuarios();
    }

    /**
     * Recupera del servidor el listado completo de perfiles de usuario.
     */
    private void cargarUsuarios() {
        if (contenedor == null) return;
        contenedor.removeAllViews();

        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String uid = doc.getString("uid");
                    String nombre = doc.getString("nombre");
                    String email = doc.getString("email");
                    String rol = doc.getString("rol");
                    
                    crearTarjetaUsuario(uid, nombre, email, rol);
                }
            }
        });
    }

    /**
     * Construye el elemento visual representativo de un perfil de usuario.
     */
    private void crearTarjetaUsuario(String uid, String nombre, String email, String rol) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(40, 40, 40, 40);
        tarjeta.setGravity(Gravity.CENTER_HORIZONTAL);
        tarjeta.setBackgroundResource(R.drawable.bg_notif_item);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        tarjeta.setLayoutParams(params);

        TextView tvD = new TextView(this);
        tvD.setText("👤 " + nombre + "\n📧 " + email + "\n🔑 ROL: " + rol);
        tvD.setTextSize(16);
        tvD.setTextColor(Color.BLACK);
        tvD.setGravity(Gravity.CENTER);
        tarjeta.addView(tvD);

        // Evento de cambio de rol
        tarjeta.setOnClickListener(v -> mostrarMenuRoles(uid, nombre));

        contenedor.addView(tarjeta);
    }

    /**
     * Despliega el menú de selección de roles administrativos.
     * 
     * @param uid    Identificador único del usuario a modificar.
     * @param nombre Nombre del perfil seleccionado.
     */
    private void mostrarMenuRoles(String uid, String nombre) {
        String[] roles = {"DUEÑO", "VETERINARIO", "ADMIN"};
        new AlertDialog.Builder(this)
                .setTitle("Cambiar Rol: " + nombre)
                .setItems(roles, (dialog, which) -> {
                    String nuevoRol = roles[which];
                    Map<String, Object> update = new HashMap<>();
                    update.put("rol", nuevoRol);
                    
                    db.collection("users").document(uid).update(update).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Rol actualizado con éxito", Toast.LENGTH_SHORT).show();
                        Logger.log("ADMIN cambió rol de " + nombre + " a " + nuevoRol);
                        cargarUsuarios();
                    });
                })
                .show();
    }
}