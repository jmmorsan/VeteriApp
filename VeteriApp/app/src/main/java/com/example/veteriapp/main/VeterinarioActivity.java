package com.example.veteriapp.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.veteriapp.R;
import com.example.veteriapp.auth.LoginActivity;
import com.example.veteriapp.model.AnimalFact;
import com.example.veteriapp.api.AnimalApiService;
import com.example.veteriapp.utils.Logger;
import com.example.veteriapp.utils.NotificationHelper;
import com.example.veteriapp.utils.SoundManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase VeterinarioActivity.
 * 
 * Dashboard Principal del Personal Médico (Veterinario).
 * Implementa SnapshotListeners globales para alertas clínicas en tiempo real.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0.5 Parche Robustez Notificaciones
 */
public class VeterinarioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView tvAnimalFact;
    private LinearLayout contenedorNoticias;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isFirstLaunchCitas = true;
    private boolean isFirstLaunchMascotas = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Estética Corporativa
        getWindow().setStatusBarColor(Color.parseColor("#4CAF50"));
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_veterinario);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        SoundManager.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout_vet);
        NavigationView navigationView = findViewById(R.id.nav_view_vet);
        navigationView.setNavigationItemSelectedListener(this);

        // Filtrado de Menú
        Menu menu = navigationView.getMenu();
        if (menu.findItem(R.id.nav_mis_mascotas) != null) menu.findItem(R.id.nav_mis_mascotas).setVisible(false);
        if (menu.findItem(R.id.nav_pedir_cita) != null) menu.findItem(R.id.nav_pedir_cita).setVisible(false);
        if (menu.findItem(R.id.nav_gestion_roles) != null) menu.findItem(R.id.nav_gestion_roles).setVisible(false);
        if (menu.findItem(R.id.nav_limpieza_datos) != null) menu.findItem(R.id.nav_limpieza_datos).setVisible(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tvAnimalFact = findViewById(R.id.tvAnimalFact);
        contenedorNoticias = findViewById(R.id.contenedorNoticias);

        actualizarHeader(navigationView, mAuth.getCurrentUser());
        setupFirestoreListeners();
        cargarDatoCurioso();
        cargarMuroNoticias();

        findViewById(R.id.btnNotificaciones).setOnClickListener(v -> startActivity(new Intent(this, NotificacionesActivity.class)));
        findViewById(R.id.fabChat).setOnClickListener(v -> startActivity(new Intent(this, BandejaChatActivity.class)));
    }

    private void actualizarHeader(NavigationView nav, FirebaseUser user) {
        View headerView = nav.getHeaderView(0);
        TextView tvNombre = headerView.findViewById(R.id.tvHeaderNombre);
        TextView tvEmail = headerView.findViewById(R.id.tvHeaderEmail);
        ImageView ivHeaderFoto = headerView.findViewById(R.id.ivHeaderFoto);

        if (ivHeaderFoto != null) ivHeaderFoto.setImageResource(R.drawable.vete_foto);
        if (user != null) {
            tvEmail.setText(user.getEmail());
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(doc -> {
                if (doc.exists()) tvNombre.setText(doc.getString("nombre"));
            });
        }
    }

    /**
     * Sincroniza las alertas destinadas a la CLINICA.
     */
    private void setupFirestoreListeners() {
        View puntoRojo = findViewById(R.id.puntoRojoNotif);

        db.collection("notificaciones")
                .whereEqualTo("uidDestinatario", "CLINICA")
                .whereEqualTo("leida", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("VeteriApp", "Error Listener Clínica", error);
                        return;
                    }
                    
                    if (value != null) {
                        boolean hayNoticias = !value.isEmpty();
                        if (puntoRojo != null) puntoRojo.setVisibility(hayNoticias ? View.VISIBLE : View.GONE);
                        
                        Log.d("VeteriApp", "Notif Clínica - Bell: " + hayNoticias);

                        // Alerta local (Banner) si llegan nuevas mientras la App está abierta
                        if (hayNoticias && !isFirstLaunchCitas) {
                             NotificationHelper.showNotification(this, "VeteriApp Clínica", "Tienes nuevas solicitudes pendientes");
                             SoundManager.playPop();
                        }
                        isFirstLaunchCitas = false;
                    }
                });
    }

    private void cargarDatoCurioso() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dogapi.dog/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AnimalApiService service = retrofit.create(AnimalApiService.class);
        service.getRandomDogFact().enqueue(new Callback<AnimalFact>() {
            @Override
            public void onResponse(Call<AnimalFact> call, Response<AnimalFact> response) {
                if (response.isSuccessful() && response.body() != null) {
                    traducirYMostrar(response.body().getFact());
                }
            }
            @Override
            public void onFailure(Call<AnimalFact> call, Throwable t) {
                tvAnimalFact.setText("¿Sabías que los perros son los mejores amigos del hombre?");
            }
        });
    }

    private void traducirYMostrar(String texto) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)
                .build();
        final Translator translator = Translation.getClient(options);
        translator.downloadModelIfNeeded().addOnSuccessListener(unused -> {
            translator.translate(texto).addOnSuccessListener(t -> {
                tvAnimalFact.setText(t);
                translator.close();
            });
        });
    }

    private void cargarMuroNoticias() {
        db.collection("noticias").orderBy("timestamp", Query.Direction.DESCENDING).limit(5)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    contenedorNoticias.removeAllViews();
                    for (QueryDocumentSnapshot doc : value) {
                        crearFilaNoticia(doc.getString("titulo"), doc.getString("contenido"));
                    }
                });
    }

    private void crearFilaNoticia(String titulo, String contenido) {
        TextView tvT = new TextView(this);
        tvT.setText(titulo);
        tvT.setTextColor(Color.parseColor("#388E3C"));
        tvT.setTextSize(16);
        tvT.setTypeface(null, android.graphics.Typeface.BOLD);
        contenedorNoticias.addView(tvT);

        TextView tvC = new TextView(this);
        tvC.setText(contenido);
        tvC.setPadding(0, 0, 0, 30);
        contenedorNoticias.addView(tvC);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SoundManager.playClick();
        if (id == R.id.nav_revisar_pacientes) startActivity(new Intent(this, AdminMascotasActivity.class));
        else if (id == R.id.nav_gestionar_citas) startActivity(new Intent(this, AdminCitasActivity.class));
        else if (id == R.id.nav_publicar_noticia) mostrarDialogoPublicarNoticia();
        else if (id == R.id.nav_memorial) startActivity(new Intent(this, MemorialActivity.class));
        else if (id == R.id.nav_cerrar_sesion) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mostrarDialogoPublicarNoticia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Publicar Noticia Global 📢");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        final EditText etT = new EditText(this); etT.setHint("Título"); layout.addView(etT);
        final EditText etC = new EditText(this); etC.setHint("Contenido"); layout.addView(etC);
        builder.setView(layout);
        builder.setPositiveButton("Publicar", (dialog, which) -> {
            Map<String, Object> n = new HashMap<>();
            n.put("titulo", etT.getText().toString());
            n.put("contenido", etC.getText().toString());
            n.put("timestamp", com.google.firebase.Timestamp.now());
            db.collection("noticias").add(n).addOnSuccessListener(doc -> {
                Toast.makeText(this, "Noticia publicada", Toast.LENGTH_SHORT).show();
                Logger.log("Veterinario publicó noticia: " + etT.getText().toString());
            });
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}