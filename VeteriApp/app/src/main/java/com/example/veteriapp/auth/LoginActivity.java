package com.example.veteriapp.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.veteriapp.R;
import com.example.veteriapp.main.AdminActivity;
import com.example.veteriapp.main.UsuarioActivity;
import com.example.veteriapp.main.VeterinarioActivity;
import com.example.veteriapp.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Clase LoginActivity.
 * 
 * Gestiona el acceso seguro de los usuarios al sistema.
 * Implementa la validación de credenciales mediante Firebase Authentication
 * y redirige dinámicamente al dashboard correspondiente según el rol del perfil.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class LoginActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private EditText etEmail, etPass;
    private Button btnLogin;
    private TextView tvIrARegistro;
    
    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseAuth mAuth;

    /**
     * Inicialización de los componentes de autenticación.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // --- CONFIGURACIÓN DE PANTALLA ADAPTATIVA ---
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
                                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.WHITE);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmailLogin);
        etPass = findViewById(R.id.etPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvIrARegistro = findViewById(R.id.tvIrARegistro);

        // Eventos de botones
        btnLogin.setOnClickListener(v -> loginUsuario());
        tvIrARegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
    }

    /**
     * Procesa el intento de inicio de sesión validando nulidades y formato.
     */
    private void loginUsuario() {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                irSegunRol(email);
            } else {
                Toast.makeText(this, "Error de acceso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Consulta el rol del usuario autenticado y despacha a la actividad principal correcta.
     * 
     * @param email Identificador de correo del usuario logueado.
     */
    private void irSegunRol(String email) {
        if (mAuth.getCurrentUser() == null) return;
        
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener(doc -> {
            String rolStr = "";
            Object rolObj = doc.get("rol");
            
            // Puente de compatibilidad para el campo rol
            if (rolObj instanceof String) rolStr = (String) rolObj;
            else if (rolObj instanceof List) {
                List<?> l = (List<?>) rolObj;
                if (!l.isEmpty()) rolStr = l.get(0).toString();
            }

            Logger.log("Usuario logueado: " + email + " con rol: " + rolStr);

            if ("ADMIN".equals(rolStr)) startActivity(new Intent(this, AdminActivity.class));
            else if ("VETERINARIO".equals(rolStr)) startActivity(new Intent(this, VeterinarioActivity.class));
            else startActivity(new Intent(this, UsuarioActivity.class));
            finish();
        });
    }
}