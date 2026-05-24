# 🐞 Registro de Incidencias - VeteriApp Release 1.0

Este documento detalla los retos técnicos y errores críticos identificados durante el ciclo de desarrollo y refinamiento de la aplicación, así como las soluciones de ingeniería aplicadas para garantizar la estabilidad final.

---

### 1. Gestión de Interfaz y Usabilidad (UX/UI)

| Incidencia | Descripción | Gravedad | Resolución |
| :--- | :--- | :--- | :--- |
| **I-01: Obstrucción por Teclado** | En pantallas de entrada de datos (Login/Chat), el teclado virtual tapaba los campos de texto o botones de acción. | **Crítica** | Implementación de `adjustResize` en el Manifiesto y uso de `ScrollView` con pesos dinámicos en los layouts. |
| **I-02: Conflicto Modo Inmersivo** | Al activar la pantalla completa, Android desactivaba el redimensionado del teclado. | **Alta** | Reconfiguración de flags de sistema a `LAYOUT_STABLE` y eliminación de `windowFullscreen` en el tema base. |
| **I-03: Franjas Cromáticas Inconsistentes** | Aparición de barras de color azul/púrpura en la zona de estado (StatusBar) que rompían la estética verde corporativa. | **Baja** | Unificación manual del color de barra de estado vía código (`setStatusBarColor`) y actualización de paleta en `themes.xml`. |

### 2. Persistencia y Backend (Firebase Firestore)

| Incidencia | Descripción | Gravedad | Resolución |
| :--- | :--- | :--- | :--- |
| **I-04: Conflicto de Tipos de Datos** | Fallos en la App al intentar leer registros antiguos (String) frente a la nueva arquitectura robusta (Numbers/Timestamps). | **Muy Alta** | Implementación de "Safe Getters" y lógica de puente para convertir cualquier tipo de dato a String/Date de forma segura. |
| **I-05: Pérdida de Relación Dueño-Mascota** | Al cambiar el esquema de IDs, las mascotas no aparecían en el panel del dueño por falta de coincidencia entre UID y ID numérico. | **Crítica** | Implementación de búsqueda híbrida: el sistema ahora valida la propiedad tanto por el UID largo de Firebase como por el ID secuencial. |
| **I-06: Desaparición de Registros por Ordenación** | El uso de `.orderBy("id_mascota")` ocultaba registros que no poseían dicho campo físicamente en Firestore. | **Alta** | Eliminación de la ordenación forzada por servidor, delegando la gestión de visualización a una lógica más tolerante en el cliente. |

### 3. Lógica de Negocio y Seguridad

| Incidencia | Descripción | Gravedad | Resolución |
| :--- | :--- | :--- | :--- |
| **I-07: Fuga de Privilegios en Menús** | Los usuarios visualizaban opciones de menú correspondientes a otros roles tras la unificación de diseños. | **Media** | Inyección de lógica de filtrado programático: la App oculta elementos del `NavigationView` según el rol detectado en el login. |
| **I-08: Fallo en Notificaciones de Citas** | El veterinario no recibía alertas visuales inmediatas cuando un cliente solicitaba una nueva cita médica. | **Alta** | Vinculación del proceso de guardado de cita con la emisión de un documento en la colección `notificaciones` dirigido a "CLINICA". |

---

### 📘 Conclusión Técnica
Todas las incidencias críticas han sido resueltas. El sistema actual presenta una arquitectura **tolerante a fallos de datos** y una interfaz **adaptativa inteligente**, cumpliendo con los estándares de calidad exigidos para una versión Release 1.0.
