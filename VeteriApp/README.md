# 🐾 VeteriApp - Release 1.0

**VeteriApp** es un ecosistema digital integral para la gestión de servicios veterinarios, diseñado para transformar la interacción tradicional entre el centro clínico y el propietario de la mascota. El software combina la robustez de un sistema de gestión clínica (ERP) con la sensibilidad de un espacio conmemorativo único.

![Versión](https://img.shields.io/badge/Versi%C3%B3n-1.0_Release-4CAF50?style=for-the-badge)
![Android SDK](https://img.shields.io/badge/Android-SDK_35-388E3C?style=for-the-badge&logo=android)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)

---

### 🎯 Propósito del Proyecto
En la actualidad, las clínicas veterinarias pequeñas y medianas carecen de herramientas móviles inmersivas que permitan una comunicación bidireccional fluida con el dueño. **VeteriApp** unifica la administración operativa (censos, citas, noticias) con servicios de valor añadido como la **Inteligencia Artificial** para curiosidades animales y el **Puente del Arcoíris**, un memorial multimedia dedicado a honrar el vínculo emocional con las mascotas que ya no están.

### 🚀 Características Innovadoras
*   **Modo Inmersivo Adaptativo**: Interfaz optimizada para aprovechar el 100% de la pantalla (especialmente en dispositivos de gran formato como el Samsung S25+), con gestión inteligente de teclado virtual (`adjustResize`).
*   **IA Local (Edge Computing)**: Integración de **Google ML Kit** para la traducción instantánea de datos clínicos y curiosidades obtenidos de APIs externas, garantizando la privacidad al procesar los datos íntegramente en el dispositivo.
*   **Arquitectura de Datos Robusta**: Implementación de identificadores secuenciales específicos (`id_mascota`, `id_cita`, `id_log`) y relaciones híbridas mediante UIDs inmutables de Firebase.
*   **Gestión Multimedia Atómica**: Sistema de serialización **Base64** para la carga instantánea de fotografías de pacientes dentro del mismo flujo de datos de Firestore.

### 👥 Roles de Usuario
La plataforma implementa un sistema de control de acceso basado en roles (**RBAC**) con 3 niveles de privilegios:

#### 🧑‍🤝‍🧑 Dueño (Cliente)
*   Gestión de su censo personal de mascotas vinculadas por UID.
*   Solicitud de citas clínicas categorizadas (General, Vacuna, Peluquería).
*   Comunicación directa vía **Chat en tiempo real** con la clínica.
*   Acceso y edición de dedicatorias personales en el Memorial.

#### 👨‍⚕️ Veterinario (Personal Médico)
*   Supervisión y validación de altas de nuevos pacientes.
*   **Gestión de Agenda**: Confirmación, rechazo y archivo histórico de citas.
*   **Multichat**: Bandeja de entrada centralizada para atender a múltiples clientes simultáneamente.
*   Publicación de noticias sanitarias y avisos de la clínica en tiempo real.

#### 🛡️ Administrador (Mantenimiento Técnico)
*   **Dashboard Estadístico**: Métricas agregadas de usuarios y mascotas activas.
*   **Gestión de Privilegios**: Reasignación de roles mediante tipos enumerados (Enums).
*   **Auditoría de Sistema**: Supervisión de logs técnicos y operativos secuenciales.
*   Mantenimiento Atómico: Borrado masivo de datos obsoletos mediante *WriteBatch*.

### 🛠️ Tecnologías Utilizadas
*   **Lenguaje**: Java 17.
*   **Entorno**: Android Studio Ladybug.
*   **Backend**: Google Firebase Firestore (NoSQL en tiempo real).
*   **Seguridad**: Firebase Authentication.
*   **IA**: Google ML Kit (Translation API).
*   **API REST**: Retrofit 2.9.0 + GSON (Dog API).
*   **Multimedia**: SoundPool (Sonidos de baja latencia).

### 🏗️ Arquitectura del Sistema
El proyecto sigue una **Arquitectura en Capas** y un **Esquema de Datos en Estrella Lógico**:
*   **Capa de Modelos (`model`)**: Entidades POJO y Enums que blindan la lógica de negocio.
*   **Capa de Presentación (`auth` / `main`)**: Actividades optimizadas con diseño inmersivo.
*   **Capa de Utilidades (`utils`)**: Servicios transversales de Auditoría y Multimedia.
*   **Esquema en Estrella**: Organización radial de la información donde Usuarios y Mascotas actúan como núcleo central.

### 📂 Estructura del Proyecto
```text
com.example.veteriapp
├── api/                # Clientes API REST (Retrofit)
├── auth/               # Gestión de Acceso (Login, Registro, Splash)
├── main/               # Lógica operativa de Dueño, Vet y Admin
├── model/              # Modelos de datos y Enumeraciones (Enums)
└── utils/              # Servicios de Soporte (Logs, Sonido, Notificaciones)
```

### 📚 Configuración y Ejecución Local

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/tu_usuario/VeteriApp.git
    ```
2.  **Configurar Firebase**:
    *   Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).
    *   Habilita *Email/Password Auth* y *Cloud Firestore*.
    *   Descarga el archivo `google-services.json` y colócalo en el directorio `app/`.
3.  **Compilar**:
    *   Abre el proyecto en Android Studio.
    *   Sincroniza Gradle.
    *   Ejecuta en un dispositivo con API 35.

---
**Desarrollado por:** Juan Manuel Moreno Sánchez 🚀🏅🐾
