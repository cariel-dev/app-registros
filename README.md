# 🏋️‍♂️ App de Medidas Corporales, Peso y Plicometría

<div align="center">

### 📲 Descarga Directa del Instalador APK (Android)

[![Descargar APK](https://img.shields.io/badge/Descargar%20APK-Android%20v1.0.0-emerald?style=for-the-badge&logo=android&logoColor=white)](https://github.com/cariel-dev/app-registros/releases/latest/download/app-debug.apk)
[![Última Versión](https://img.shields.io/github/v/release/cariel-dev/app-registros?style=for-the-badge&logo=github&color=blue)](https://github.com/cariel-dev/app-registros/releases/latest)
[![Portal Web](https://img.shields.io/badge/Portal%20Web-GitHub%20Pages-8b5cf6?style=for-the-badge&logo=googlechrome&logoColor=white)](https://cariel-dev.github.io/app-registros/)

**[👉 Haz clic aquí para descargar directamente `app-debug.apk`](https://github.com/cariel-dev/app-registros/releases/latest/download/app-debug.apk)**  
*(Versión v1.0.0 • Peso: ~19 MB • Compatible con Android 8.0 o superior)*

</div>

---

### 📥 Cómo instalar la app en tu teléfono Android (3 pasos):
1. **Descarga el archivo:** Pulsa el botón de descarga anterior desde el navegador de tu celular Android.
2. **Confirmar descarga:** Si Android muestra *"El archivo puede ser dañino"*, pulsa en **Descargar de todos modos** (aviso estándar para apps fuera de Google Play).
3. **Instalar:** Pulsa la notificación de descarga o abre el archivo en tu carpeta *Descargas* y presiona **Instalar**. *(Si te pide permisos, activa «Permitir desde esta fuente»)*.

---

Aplicación móvil nativa en **Android (Kotlin + Jetpack Compose)** y portal web complementario para el seguimiento detallado de composición corporal y antropometría:
* **Peso y Fecha/Hora exacta:** Registro preciso con marcas de tiempo.
* **Circunferencias en centímetros (cm):** Cuello, hombros, pecho, cintura, cadera, bíceps (izq/der), antebrazos (izq/der), muslos (izq/der) y pantorrillas (izq/der).
* **Plicometría / Pliegues cutáneos (mm):** Tríceps, subescapular, suprailíaco, abdominal, muslo anterior, pectoral y axilar medio.
* **Cálculo automático de grasa corporal (%):** Estimación en tiempo real mediante la fórmula Jackson-Pollock.
* **Arquitectura Offline-First:** Los datos se guardan **de inmediato en el teléfono (Room DB / SQLite)** sin importar si tienes internet o no. En cuanto el teléfono detecta señal Wi-Fi o datos móviles, se sincroniza en segundo plano con **Supabase (PostgreSQL)**.
* **Login Simple (Sin correos):** Solo necesitas crear un **Nombre de Usuario** y una **Contraseña**, sin pedir correos ni enlaces de verificación.
* **Portabilidad Total:** Exporta todas tus mediciones con un clic a **CSV (Excel / Google Sheets)** o **JSON**.

---

## 📁 Estructura del Código

```
app-registros/
├── app/src/main/java/com/app/registros/
│   ├── MainActivity.kt                 # Actividad principal Single-Activity (Compose)
│   ├── AppRegistrosApplication.kt      # Inicialización de la aplicación Android
│   ├── data/
│   │   ├── local/                      # Base de datos local SQLite (Room)
│   │   │   ├── AppDatabase.kt          # Instancia Room DB en el teléfono
│   │   │   ├── MeasurementEntity.kt    # Entidad de medidas con flags de sync
│   │   │   └── MeasurementDao.kt       # Consultas locales rápidas
│   │   ├── model/
│   │   │   └── BodyMeasurement.kt      # Modelo de dominio serializable con Jackson-Pollock
│   │   ├── remote/
│   │   │   └── SupabaseProvider.kt     # Conexión al backend PostgreSQL de Supabase
│   │   ├── repository/
│   │   │   ├── AuthRepository.kt       # Autenticación directa por usuario y contraseña
│   │   │   └── MeasurementRepository.kt# Repositorio Offline-First
│   │   └── sync/
│   │       └── SyncManager.kt          # Sincronizador de red automático teléfono ↔ nube
│   └── ui/
│       ├── navigation/                 # Rutas declarativas (Screen y AppNavigation)
│       ├── theme/                      # Paleta atlética moderna (Deep Slate & Emerald)
│       └── screens/
│           ├── auth/                   # Login y Registro por usuario/contraseña
│           ├── measurements/           # Historial, badges de sincronización y formulario
│           └── export/                 # Exportación instantánea a CSV y compartir
├── supabase/
│   └── schema.sql                      # Tablas PostgreSQL, tipos precisos y seguridad RLS
├── web/
│   └── index.html                      # Portal web para consultar y registrar medidas desde el PC
└── README.md
```

---

## 🚀 Puesta en Marcha Rápida

### 1. Configurar la Base de Datos en Supabase (Gratis)
1. Entra a [https://supabase.com](https://supabase.com) y crea tu proyecto.
2. Abre la pestaña **SQL Editor** en el menú de Supabase.
3. Copia todo el contenido de [`supabase/schema.sql`](supabase/schema.sql), pégalo en el editor y haz clic en **Run**.
   * *Esto creará la tabla `mediciones_corporales` con todos los campos métricos y las políticas de seguridad (RLS).*
4. En Supabase ve a **Authentication** -> **Providers** -> **Email**:
   * Desmarca la casilla **"Confirm email"** para que los usuarios puedan iniciar sesión de inmediato sin verificar correos.
5. Ve a **Project Settings** -> **API** y copia tu **Project URL** y tu **Anon Key**.

---

### 2. Probar el Portal Web Inmediatamente
Puedes usar y probar la aplicación web desde tu navegador de inmediato:
1. Abre [`web/index.html`](web/index.html) en tu navegador.
2. Pega tu URL y Anon Key de Supabase en la barra superior.
3. Haz clic en **"Crear cuenta (sin correo)"**, escribe tu usuario (ej. `hugo_fit`) y tu contraseña.
4. Agrega una medición con peso, cintura y pliegues.
5. Prueba el botón **"Exportar CSV"** para descargar tu hoja de cálculo.

---

### 3. Abrir y Ejecutar la App Móvil Android
1. Abre [`app/src/main/java/com/app/registros/data/remote/SupabaseProvider.kt`](app/src/main/java/com/app/registros/data/remote/SupabaseProvider.kt).
2. Pega tu `SUPABASE_URL` y `SUPABASE_ANON_KEY`.
3. Abre esta carpeta (`/home/hugo/dev/app-registros`) en **Android Studio**.
4. Conecta tu teléfono Android o inicia el emulador y presiona **Run (▶️)**.
5. Inicia sesión con el mismo usuario.
6. **Prueba Offline:** Apaga el Wi-Fi y los datos móviles de tu teléfono. Registra una medición. Verás que se guarda de inmediato con la etiqueta `📱 En teléfono`. Vuelve a activar el internet y verás cómo el icono cambia automáticamente a `☁️ En nube` y se refleja en el portal web.
