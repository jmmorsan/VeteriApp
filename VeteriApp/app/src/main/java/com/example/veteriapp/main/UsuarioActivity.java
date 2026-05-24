package com.example.veteriapp.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.veteriapp.R;
import com.example.veteriapp.auth.LoginActivity;
import com.example.veteriapp.model.AnimalFact;
import com.example.veteriapp.api.AnimalApiService;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase UsuarioActivity.
 * 
 * Dashboard Principal del Usuario con rol Dueño.
 * Gestiona el muro de noticias, curiosidades animales mediante IA local,
 * y acceso a la gestión de mascotas, citas y memorial.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class UsuarioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	// --- VARIABLES DE LA INTERFAZ ---
    private DrawerLayout drawerLayout;
    private TextView tvAnimalFact;
    private LinearLayout contenedorNoticias;

    // --- INSTANCIAS DE FIREBASE ---
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Método principal de creación de la actividad.
     * Inicializa componentes, configuración estética y escuchadores de datos.
     * 
     * @param savedInstanceState Estado previo de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // --- CONFIGURACIÓN ESTÉTICA DE BARRAS DE SISTEMA ---
        getWindow().setStatusBarColor(Color.parseColor("#4CAF50"));
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_usuario);

        // --- INICIALIZACIÓN DE SERVICIOS ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        SoundManager.init(this);

        // --- CONFIGURACIÓN DE TOOLBAR Y NAVEGACIÓN ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // --- FILTRADO DE MENÚ POR ROL (SEGURIDAD VISUAL) ---
        Menu menu = navigationView.getMenu();
        if (menu.findItem(R.id.nav_gestionar_citas) != null) menu.findItem(R.id.nav_gestionar_citas).setVisible(false);
        if (menu.findItem(R.id.nav_revisar_pacientes) != null) menu.findItem(R.id.nav_revisar_pacientes).setVisible(false);
        if (menu.findItem(R.id.nav_publicar_noticia) != null) menu.findItem(R.id.nav_publicar_noticia).setVisible(false);
        if (menu.findItem(R.id.nav_gestion_roles) != null) menu.findItem(R.id.nav_gestion_roles).setVisible(false);
        if (menu.findItem(R.id.nav_limpieza_datos) != null) menu.findItem(R.id.nav_limpieza_datos).setVisible(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- VINCULACIÓN DE COMPONENTES DE VISTA ---
        tvAnimalFact = findViewById(R.id.tvAnimalFact);
        contenedorNoticias = findViewById(R.id.contenedorNoticias);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            actualizarHeaderYBienvenida(navigationView, user);
        }

        // --- CARGA DE CONTENIDO DINÁMICO ---
        cargarDatoCurioso();
        cargarMuroNoticias();

        // --- EVENTOS DE BOTONES ---
        findViewById(R.id.btnNotificaciones).setOnClickListener(v -> startActivity(new Intent(this, NotificacionesActivity.class)));
        findViewById(R.id.fabChat).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        
        // --- GESTIÓN DE NOTIFICACIONES NO LEÍDAS (PUNTO ROJO) ---
        View puntoRojo = findViewById(R.id.puntoRojoNotif);
        if (user != null && puntoRojo != null) {
            db.collection("notificaciones")
                    .whereEqualTo("uidDestinatario", user.getUid())
                    .whereEqualTo("leida", false)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) return;
                        puntoRojo.setVisibility((value != null && !value.isEmpty()) ? View.VISIBLE : View.GONE);
                    });
        }
    }

    /**
     * Sincroniza la información del usuario en el encabezado del menú y bienvenida.
     * 
     * @param nav  Referencia a la NavigationView.
     * @param user Usuario autenticado en Firebase.
     */
    private void actualizarHeaderYBienvenida(NavigationView nav, FirebaseUser user) {
        View headerView = nav.getHeaderView(0);
        TextView tvHeaderNombre = headerView.findViewById(R.id.tvHeaderNombre);
        TextView tvHeaderEmail = headerView.findViewById(R.id.tvHeaderEmail);
        ImageView ivHeaderFoto = headerView.findViewById(R.id.ivHeaderFoto);
        TextView tvBienvenida = findViewById(R.id.tvBienvenida);

        // --- ASIGNACIÓN DE FOTO GENÉRICA POR ROL ---
        if (ivHeaderFoto != null) {
            ivHeaderFoto.setImageResource(R.drawable.usuario_foto);
        }

        if (tvHeaderEmail != null) tvHeaderEmail.setText(user.getEmail());
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String nombre = document.getString("nombre");
                if (tvHeaderNombre != null) tvHeaderNombre.setText(nombre);
                if (tvBienvenida != null) tvBienvenida.setText("¡Hola, " + nombre + "! 🐾");
            }
        });
    }

    /**
     * Realiza una petición externa vía Retrofit para obtener curiosidades animales.
     */
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
                tvAnimalFact.setText("¿Sabías que los perros pueden entender hasta 250 palabras?");
            }
        });
    }

    /**
     * Utiliza Google ML Kit para traducir el dato curioso de inglés a español de forma local.
     * 
     * @param texto Cadena original en inglés.
     */
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

    /**
     * Recupera y visualiza las últimas novedades publicadas por la clínica.
     */
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

    /**
     * Construye dinámicamente un bloque visual para una noticia.
     * 
     * @param titulo    Título de la novedad sanitaria.
     * @param contenido Detalle del aviso.
     */
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

    /**
     * Gestiona las acciones al seleccionar un elemento del menú lateral.
     * 
     * @param item Elemento del menú seleccionado.
     * @return true tras procesar la navegación.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SoundManager.playClick();
        if (id == R.id.nav_mis_mascotas) startActivity(new Intent(this, MisMascotasActivity.class));
        else if (id == R.id.nav_pedir_cita) startActivity(new Intent(this, PedirCitaActivity.class));
        else if (id == R.id.nav_memorial) startActivity(new Intent(this, MemorialActivity.class));
        else if (id == R.id.nav_ajustes) mostrarDialogoAjustes();
        else if (id == R.id.nav_cerrar_sesion) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Muestra un cuadro de diálogo para la configuración de sonidos y preferencias.
     */
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

    /**
     * Gestión del cierre del menú lateral mediante el botón físico de retroceso.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}