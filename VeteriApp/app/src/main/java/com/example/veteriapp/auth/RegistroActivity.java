package com.example.veteriapp.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.veteriapp.R;
import com.example.veteriapp.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase RegistroActivity.
 * 
 * Gestiona la creación de nuevas cuentas de usuario con rol Dueño.
 * Implementa la lógica de ID secuencial id_usuario para trazabilidad administrativa
 * y persiste el perfil inicial en la colección de usuarios de Firestore.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class RegistroActivity extends AppCompatActivity {

	// --- VARIABLES DE LA INTERFAZ ---
    private EditText etNombre, etEmail, etPassword;
    private Button btnRegistrar;

    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Configuración inicial de la actividad de registro.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configuración de pantalla adaptativa y estética
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
                                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.WHITE);

        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPass);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    /**
     * Valida los datos, crea la cuenta en Firebase Auth y registra el perfil en Firestore.
     */
    private void registrarUsuario() {
        final String nombre = etNombre.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final String uid = mAuth.getCurrentUser().getUid();

                // LÓGICA DE ID_USUARIO SECUENCIAL
                db.collection("users").orderBy("id", Query.Direction.DESCENDING).limit(1).get()
                        .addOnSuccessListener(snap -> {
                            int idCalc = 1;
                            if (!snap.isEmpty()) {
                                Number ult = snap.getDocuments().get(0).getLong("id");
                                if (ult != null) idCalc = ult.intValue() + 1;
                            }

                            final int finalId = idCalc;
                            Map<String, Object> user = new HashMap<>();
                            user.put("uid", uid);
                            user.put("id", finalId);
                            user.put("nombre", nombre);
                            user.put("email", email);
                            user.put("rol", "DUEÑO");

                            db.collection("users").document(uid).set(user).addOnSuccessListener(aVoid -> {
                                Logger.log("Nuevo usuario registrado: " + email + " con ID: " + finalId);
                                finish();
                            });
                        });
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}