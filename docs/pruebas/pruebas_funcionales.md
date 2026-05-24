# ✅ Registro de Pruebas Funcionales - VeteriApp Release 1.0

Este documento certifica la validación de las funcionalidades principales de la aplicación, simulando el comportamiento real del usuario y verificando la respuesta del sistema.

---

### 1. Pruebas de Autenticación y Seguridad

| ID | Acción | Entrada | Resultado Esperado | Resultado Real |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Registro de usuario | Nombre, Email, Pass | Creación de cuenta y asignación de Rol.DUEÑO | **Correcto** |
| **TC-02** | Login por roles | Credenciales de Veterinario | Redirección inmediata a `VeterinarioActivity` | **Correcto** |
| **TC-03** | Blindaje de contraseña | "123" | Firebase Auth debe rechazar por longitud insuficiente | **Correcto** |
| **TC-04** | Cerrar sesión | Botón Logout | Purga de token y vuelta a `LoginActivity` | **Correcto** |

### 2. Gestión Operativa de Mascotas

| ID | Acción | Entrada | Resultado Esperado | Resultado Real |
| :--- | :--- | :--- | :--- | :--- |
| **TC-05** | Alta con género | Selección "MACHO" | Persistencia en Firestore del atributo `genero` | **Correcto** |
| **TC-06** | Carga de imagen | Galería -> Base64 | La imagen debe verse en el listado y detalle | **Correcto** |
| **TC-07** | ID Secuencial | Alta de nueva mascota | El sistema debe asignar `último_id + 1` | **Correcto** |
| **TC-08** | Ficha Técnica | Click en tarjeta | Recuperación de todos los datos vía Intent Extras | **Correcto** |

### 3. Comunicación y Citas Médicas

| ID | Acción | Entrada | Resultado Esperado | Resultado Real |
| :--- | :--- | :--- | :--- | :--- |
| **TC-09** | Filtro de solicitud | Dueño pide cita | Solo se muestran mascotas con estado "ACEPTADA" | **Correcto** |
| **TC-10** | Notificación Vet | Nueva cita | El Veterinario debe ver el aviso / punto rojo | **Correcto** |
| **TC-11** | Chat adaptativo | Abrir teclado | El campo de texto debe quedar sobre el teclado | **Correcto** |
| **TC-12** | Archivo histórico | Cita realizada | El Veterinario puede pasar el estado a "ARCHIVADA" | **Correcto** |

### 4. Inteligencia Artificial y Multimedia

| ID | Acción | Entrada | Resultado Esperado | Resultado Real |
| :--- | :--- | :--- | :--- | :--- |
| **TC-13** | Traducción local | Dog Fact (API) | Conversión a español mediante ML Kit offline | **Correcto** |
| **TC-14** | SoundManager | Click en menú | Reproducción de audio de baja latencia | **Correcto** |
| **TC-15** | Memorial Shared | Acceso Vet/Admin | El personal clínico puede ver el muro público | **Correcto** |

---

### 🏆 Resumen de Certificación
- **Casos ejecutados**: 15
- **Exitosos**: 15 (100%)
- **Fallidos**: 0

**VeteriApp Release 1.0** se declara funcionalmente estable y apta para su despliegue en entorno de producción.
