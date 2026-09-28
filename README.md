# 📱 App Registros: Android Nativo + Base de Datos Portable (Supabase/PostgreSQL) + Portal Web

Este proyecto implementa la arquitectura completa solicitada:
1. **App Móvil Nativa en Android:** Desarrollada con **Kotlin** y **Jetpack Compose**, arquitectura moderna (MVVM + Clean Architecture) y cliente oficial de Supabase.
2. **Base de Datos Portable (PostgreSQL / Supabase):** Cero ataduras (Zero Lock-in), exportación de datos en 1 clic (CSV, JSON, volcado SQL), autenticación segura y políticas RLS para privacidad.
3. **Portal Web Sincronizado:** Permite iniciar sesión con la misma cuenta de la app móvil para consultar, agregar o exportar los registros desde cualquier navegador web.

---

## 📁 Estructura del Proyecto

```
app-registros/
├── app/                                # Código fuente de la app nativa Android
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/app/registros/
│   │   │   ├── MainActivity.kt
│   │   │   ├── AppRegistrosApplication.kt
│   │   │   ├── data/
│   │   │   │   ├── model/Record.kt     # Modelo de datos serializable
│   │   │   │   ├── remote/SupabaseProvider.kt # Conexión a Supabase
│   │   │   │   └── repository/         # Lógica de datos (Auth y Registros)
│   │   │   └── ui/
│   │   │       ├── theme/              # Diseño Material 3
│   │   │       ├── navigation/         # Enrutamiento de pantallas
│   │   │       └── screens/            # Pantallas (Login, Registros, Exportar)
│   │   └── res/values/                 # Strings, colores y estilos
│   └── build.gradle.kts                # Dependencias Compose y Supabase
├── supabase/
│   └── schema.sql                      # Script SQL listo para crear tablas y seguridad RLS
├── web/
│   └── index.html                      # Portal web para consultar y exportar datos vía navegador
├── build.gradle.kts                    # Configuración raíz de Gradle
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Guía de Puesta en Marcha Paso a Paso

### Paso 1: Crear tu Base de Datos en Supabase (Gratis)
1. Entra a [https://supabase.com](https://supabase.com) y crea una cuenta gratuita.
2. Haz clic en **"New Project"** y dale un nombre (por ejemplo, `app-registros`).
3. Ve a la sección **SQL Editor** en el menú lateral izquierdo.
4. Abre el archivo local [`supabase/schema.sql`](supabase/schema.sql), copia todo su contenido, pégalo en el editor SQL de Supabase y presiona **"Run"**.
   * *Esto creará la tabla `registros`, los índices automáticos y las políticas de seguridad (RLS).*
5. Ve a **Project Settings** (el icono de engranaje) -> **API**.
6. Copia dos datos:
   * **Project URL** (ejemplo: `https://xyzabcdef.supabase.co`)
   * **Project API keys: `anon` `public`**

---

### Paso 2: Probar el Portal Web (Inmediato)
Puedes probar tu base de datos y la sincronización web sin esperar a compilar la app móvil:
1. Abre el archivo [`web/index.html`](web/index.html) directamente en tu navegador (doble clic o ejecútalo con `xdg-open web/index.html`).
2. En la barra superior, pega tu **URL** y **Anon Key** de Supabase y haz clic en "Conectar Supabase".
3. Regístrate con un correo y contraseña de prueba.
4. ¡Listo! Ya puedes agregar registros, borrarlos y probar los botones **"Exportar CSV"** o **"Exportar JSON"**.

---

### Paso 3: Configurar la App Nativa en Android
1. Abre [`app/src/main/java/com/app/registros/data/remote/SupabaseProvider.kt`](app/src/main/java/com/app/registros/data/remote/SupabaseProvider.kt).
2. Reemplaza `SUPABASE_URL` y `SUPABASE_ANON_KEY` con tus claves reales.
3. Abre esta carpeta (`/home/hugo/dev/app-registros`) en **Android Studio**.
4. Deja que Gradle descargue las dependencias y sincronice el proyecto.
5. Conecta tu teléfono Android (o usa el emulador) y presiona **Run (▶️)**.

---

## 🔒 ¿Cómo se garantiza la portabilidad de los datos?

1. **Desde la App Móvil:**
   - La app incluye una pantalla dedicada **"Exportar Datos"** que genera al instante un archivo CSV estándar y permite copiarlo o compartirlo a WhatsApp, Drive o Correo.
2. **Desde el Portal Web:**
   - Botones de 1 clic para descargar en formato **CSV (Excel/Sheets)** o **JSON**.
3. **Desde Supabase / PostgreSQL:**
   - Puedes ir a la consola de Supabase -> `Table Editor` -> `Export` y descargar todo en un instante.
   - Si en el futuro quieres migrar a AWS, Google Cloud, DigitalOcean o tu propio servidor, puedes descargar el volcado SQL completo (`pg_dump`) y cargarlo en cualquier base de datos PostgreSQL estándar sin cambiar ni una sola línea de tus modelos.
