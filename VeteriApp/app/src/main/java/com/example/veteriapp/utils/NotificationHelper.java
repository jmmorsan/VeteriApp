package com.example.veteriapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.veteriapp.R;

/**
 * Clase NotificationHelper.
 * 
 * Centraliza la creación y emisión de notificaciones locales en la barra de estado.
 * Implementa la lógica de canales requerida para versiones modernas de Android (API 26+)
 * y estandariza el estilo visual de los avisos del sistema.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "veteriapp_notifications";
    private static final String CHANNEL_NAME = "VeteriApp Notificaciones";
    private static final String CHANNEL_DESC = "Canal para avisos de citas y mascotas";

    /**
     * Crea el canal de notificaciones necesario para Android Oreo y superiores.
     * 
     * @param context Contexto de la aplicación.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Dispara una notificación visual en el dispositivo.
     * 
     * @param context Contexto de ejecución.
     * @param title   Título de la alerta.
     * @param message Cuerpo descriptivo del aviso.
     */
    public static void showNotification(Context context, String title, String message) {
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_veteriapp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}