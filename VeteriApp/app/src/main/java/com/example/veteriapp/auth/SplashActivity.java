package com.example.veteriapp.auth;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.veteriapp.R;
import com.example.veteriapp.main.AdminActivity;
import com.example.veteriapp.main.UsuarioActivity;
import com.example.veteriapp.main.VeterinarioActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Clase SplashActivity.
 * 
 * Pantalla de bienvenida (Intro) de la aplicación.
 * Realiza la animación inicial, reproduce la sintonía corporativa y gestiona 
 * el despacho inteligente por roles basado en la persistencia de Firebase.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class SplashActivity extends AppCompatActivity {

	// --- VARIABLES DE ESTADO Y SERVICIO ---
    private FirebaseAuth mAuth;
    private MediaPlayer mediaPlayer;

    /**
     * Inicializa la secuencia de bienvenida y lógica de redirección.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Activación del Modo Inmersivo Total (Ajuste S25+)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_splash);

        // --- ANIMACIÓN DE LOGO ---
        ImageView logo = findViewById(R.id.ivLogoSplash);
        if (logo != null) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_zoom);
            logo.startAnimation(anim);
        }

        // --- REPRODUCCIÓN DE SINTONÍA ---
        try {
            int resId = getResources().getIdentifier("intro_sound", "raw", getPackageName());
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();

        // --- LÓGICA DE DESPACHO INTELIGENTE (Post-Delay) ---
        new Handler().postDelayed(() -> {
            if (mAuth.getCurrentUser() != null) {
                String uid = mAuth.getCurrentUser().getUid();
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener(doc -> {
                            String rolStr = "";
                            Object rolObj = doc.get("rol");
                            
                            // Puente de compatibilidad de roles (String/List)
                            if (rolObj instanceof String) {
                                rolStr = (String) rolObj;
                            } else if (rolObj instanceof List) {
                                List<?> roles = (List<?>) rolObj;
                                if (!roles.isEmpty()) rolStr = roles.get(0).toString();
                            }

                            if ("ADMIN".equals(rolStr)) {
                                startActivity(new Intent(this, AdminActivity.class));
                            } else if ("VETERINARIO".equals(rolStr)) {
                                startActivity(new Intent(this, VeterinarioActivity.class));
                            } else {
                                startActivity(new Intent(this, UsuarioActivity.class));
                            }
                            finish();
                        });
            } else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }, 3500);
    }

    /**
     * Libera los recursos multimedia al destruir la actividad.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}