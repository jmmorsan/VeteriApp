package com.example.veteriapp.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.veteriapp.R;
import com.example.veteriapp.auth.LoginActivity;
import com.example.veteriapp.utils.Logger;
import com.example.veteriapp.utils.SoundManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Clase AdminActivity.
 * 
 * Panel de Gestión Técnica y Auditoría del sistema (Administrador).
 * Implementa SnapshotListeners globales para alertas clínicas (Punto Rojo).
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0.5 Parche Robustez Notificaciones
 */
public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout contenedorLogs;
    private TextView tvCountUsuarios, tvCountMascotas;
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Estética Corporativa
        getWindow().setStatusBarColor(Color.parseColor("#4CAF50"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_admin);

        contenedorLogs = findViewById(R.id.contenedorLogs);
        tvCountUsuarios = findViewById(R.id.tvCountUsuarios);
        tvCountMascotas = findViewById(R.id.tvCountMascotas);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        SoundManager.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        NavigationView navigationView = findViewById(R.id.nav_view_admin);
        navigationView.setNavigationItemSelectedListener(this);

        // Filtrado de Menú
        Menu menu = navigationView.getMenu();
        if (menu.findItem(R.id.nav_mis_mascotas) != null) menu.findItem(R.id.nav_mis_mascotas).setVisible(false);
        if (menu.findItem(R.id.nav_pedir_cita) != null) menu.findItem(R.id.nav_pedir_cita).setVisible(false);
        if (menu.findItem(R.id.nav_gestionar_citas) != null) menu.findItem(R.id.nav_gestionar_citas).setVisible(false);
        if (menu.findItem(R.id.nav_revisar_pacientes) != null) menu.findItem(R.id.nav_revisar_pacientes).setVisible(false);
        if (menu.findItem(R.id.nav_publicar_noticia) != null) menu.findItem(R.id.nav_publicar_noticia).setVisible(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            actualizarHeader(navigationView, currentUser);
        }

        cargarEstadisticas();
        cargarLogs();

        findViewById(R.id.btnNotificaciones).setOnClickListener(v -> startActivity(new Intent(this, NotificacionesActivity.class)));
        
        // --- GESTIÓN DE NOTIFICACIONES GLOBALES (PUNTO ROJO) ---
        View puntoRojo = findViewById(R.id.puntoRojoNotif);
        if (puntoRojo != null) {
            db.collection("notificaciones")
                    .whereEqualTo("uidDestinatario", "CLINICA")
                    .whereEqualTo("leida", false)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.e("VeteriApp", "Error Listener Admin", error);
                            return;
                        }
                        puntoRojo.setVisibility((value != null && !value.isEmpty()) ? View.VISIBLE : View.GONE);
                        Log.d("VeteriApp", "Notif Admin - Bell: " + (value != null && !value.isEmpty()));
                    });
        }
    }

    private void actualizarHeader(NavigationView nav, FirebaseUser user) {
        View headerView = nav.getHeaderView(0);
        TextView tvHeaderNombre = headerView.findViewById(R.id.tvHeaderNombre);
        TextView tvHeaderEmail = headerView.findViewById(R.id.tvHeaderEmail);
        ImageView ivHeaderFoto = headerView.findViewById(R.id.ivHeaderFoto);

        if (ivHeaderFoto != null) ivHeaderFoto.setImageResource(R.drawable.admin_foto);
        if (tvHeaderEmail != null) tvHeaderEmail.setText(user.getEmail());
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
            if (document.exists() && tvHeaderNombre != null) {
                tvHeaderNombre.setText(document.getString("nombre"));
            }
        });
    }

    private void cargarEstadisticas() {
        db.collection("users").get().addOnSuccessListener(query -> {
            if (tvCountUsuarios != null) tvCountUsuarios.setText(String.valueOf(query.size()));
        });
        db.collection("mascotas").get().addOnSuccessListener(query -> {
            if (tvCountMascotas != null) tvCountMascotas.setText(String.valueOf(query.size()));
        });
    }

    private void cargarLogs() {
        if (contenedorLogs == null) return;
        db.collection("logs").orderBy("timestamp", Query.Direction.DESCENDING).limit(10)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    contenedorLogs.removeAllViews();
                    for (QueryDocumentSnapshot doc : value) {
                        TextView tv = new TextView(this);
                        tv.setText("• " + doc.getString("mensaje"));
                        tv.setTextSize(13);
                        tv.setPadding(0, 5, 0, 5);
                        contenedorLogs.addView(tv);
                    }
                });
    }

    private void mostrarDialogoLimpieza() {
        String[] opciones = {"🧹 Limpiar Mascotas Rechazadas", "🧹 Limpiar Citas Rechazadas", "📂 Eliminar Citas Archivadas"};
        new AlertDialog.Builder(this)
                .setTitle("Mantenimiento de Sistema")
                .setItems(opciones, (dialog, which) -> {
                    SoundManager.playClick();
                    if (which == 0) ejecutarLimpieza("mascotas", "RECHAZADA");
                    else if (which == 1) ejecutarLimpieza("citas", "RECHAZADA");
                    else ejecutarLimpieza("citas", "ARCHIVADA");
                })
                .show();
    }

    private void ejecutarLimpieza(String coleccion, String valor) {
        db.collection(coleccion).whereEqualTo("estado", valor).get().addOnSuccessListener(query -> {
            com.google.firebase.firestore.WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot doc : query) batch.delete(doc.getReference());
            batch.commit().addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Limpieza completada", Toast.LENGTH_SHORT).show();
                Logger.log("Mantenimiento: Limpieza de " + coleccion);
                cargarEstadisticas();
            });
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SoundManager.playClick();
        if (id == R.id.nav_cerrar_sesion) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_limpieza_datos) mostrarDialogoLimpieza();
        else if (id == R.id.nav_gestion_roles) startActivity(new Intent(this, GestionRolesActivity.class));
        else if (id == R.id.nav_memorial) startActivity(new Intent(this, MemorialActivity.class));
        else if (id == R.id.nav_ajustes) mostrarDialogoAjustes();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mostrarDialogoAjustes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajustes ⚙️");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 40);
        SwitchCompat sw = new SwitchCompat(this);
        sw.setText("Silenciar sonidos");
        sw.setChecked(SoundManager.isMuted());
        sw.setOnCheckedChangeListener((b, checked) -> SoundManager.toggleMute());
        layout.addView(sw);
        builder.setView(layout);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}