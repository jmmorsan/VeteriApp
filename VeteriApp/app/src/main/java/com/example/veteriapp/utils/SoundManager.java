package com.example.veteriapp.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

/**
 * Clase SoundManager.
 * 
 * Centraliza la gestión de efectos sonoros de baja latencia para la interfaz.
 * Utiliza SoundPool para una reproducción eficiente de recursos multimedia
 * y gestiona el estado de silencio persistente durante la sesión.
 * 
 * @author Juan Manuel Moreno Sánchez
 * @version 1.0 VeteriApp Release
 */
public class SoundManager {

    private static SoundPool soundPool;
    private static int clickSound, popSound;
    private static boolean muted = false;

    /**
     * Inicializa el pool de sonidos y carga los recursos de audio.
     * 
     * @param context Contexto de la aplicación.
     */
    public static void init(Context context) {
        if (soundPool == null) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(attrs)
                    .build();

            clickSound = loadSound(context, "click_sound");
            popSound = loadSound(context, "pop_sound");
        }
    }

    /**
     * Carga un recurso de audio desde la carpeta raw de forma segura.
     */
    private static int loadSound(Context context, String name) {
        int resId = context.getResources().getIdentifier(name, "raw", context.getPackageName());
        if (resId != 0) {
            return soundPool.load(context, resId, 1);
        }
        return 0;
    }

    /**
     * Reproduce el sonido de clic de la interfaz si no está silenciado.
     */
    public static void playClick() {
        if (soundPool != null && !muted && clickSound != 0) {
            soundPool.play(clickSound, 1, 1, 0, 0, 1);
        }
    }

    /**
     * Reproduce el sonido de notificación si no está silenciado.
     */
    public static void playPop() {
        if (soundPool != null && !muted && popSound != 0) {
            soundPool.play(popSound, 1, 1, 0, 0, 1);
        }
    }

    /**
     * Alterna el estado de silencio de la aplicación.
     */
    public static void toggleMute() {
        muted = !muted;
    }

    /**
     * Comprueba si la aplicación está en modo silencio.
     */
    public static boolean isMuted() {
        return muted;
    }
}