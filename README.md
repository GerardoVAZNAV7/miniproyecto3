# Red Social UAS — Mini Proyecto 3

Aplicación Android nativa desarrollada en **Kotlin + Jetpack Compose** que funciona como una red social local: permite publicar fotos con título, descripción y fecha, verlas en un feed y administrarlas desde el perfil. El contenido se guarda en el dispositivo con **Room**, por lo que se conserva al cerrar la app.

Desarrollo Móvil Nativo — FIM / UAS.

## Cómo está construido

- **Lenguaje:** Kotlin 2.0.21
- **UI:** Jetpack Compose (Material 3) + Navigation Compose
- **Persistencia:** Room 2.6.1 (KSP)
- **Imágenes:** Coil Compose
- **Arquitectura:** MVVM, separada en `data` / `domain` / `presentation`
- **Build:** Gradle 8.5.2 con Kotlin DSL (`.kts`)
- **SDK:** `minSdk 24` (Android 7.0), `targetSdk` / `compileSdk 34`

Flujo general: `MainActivity` carga `AppRaiz`, que expone el `PublicacionViewModel` y la navegación inferior. El ViewModel usa `PublicacionRepository`, el cual accede a Room a través de `PublicacionDao` para leer y escribir las publicaciones.

## Distribución de carpetas

```
Miniproyecto03/
├── app/
│   ├── build.gradle.kts          Configuración del módulo: SDK, dependencias Compose/Room/Coil
│   └── src/main/
│       ├── AndroidManifest.xml   Permisos, actividades y FileProvider
│       ├── java/com/example/miniproyecto03/
│       │   ├── MainActivity.kt           Punto de entrada de la app
│       │   ├── data/                     Modelo y acceso a datos
│       │   │   ├── Publicacion.kt        Entidad Room de una publicación
│       │   │   ├── PublicacionDao.kt     Consultas (insertar, listar, eliminar)
│       │   │   └── PublicacionRepository.kt  Repositorio que media entre ViewModel y DAO
│       │   ├── domain/
│       │   │   └── AppDatabase.kt        Base de datos Room y datos de ejemplo
│       │   ├── presentation/             Pantallas y lógica de UI (MVVM)
│       │   │   ├── AppRaiz.kt            Contenedor raíz + navegación
│       │   │   ├── BarraNavegacion.kt    Barra inferior estilo TikTok
│       │   │   ├── FeedScreen.kt         Feed vertical de publicaciones
│       │   │   ├── NuevaPublicacionScreen.kt  Alta de publicación (cámara/galería)
│       │   │   ├── PerfilScreen.kt       Perfil, cuadrícula, detalle y eliminar
│       │   │   ├── PublicacionViewModel.kt    Estado y operaciones de publicaciones
│       │   │   └── SplashScreen.kt       Pantalla de inicio
│       │   ├── util/
│       │   │   └── FotoUtils.kt          Permisos, archivos temporales y fechas
│       │   └── ui/theme/
│       │       └── Theme.kt              Colores y tipografía de la app
│       └── res/
│           ├── drawable-nodpi/           Logo e imágenes de ejemplo
│           ├── mipmap-*/                 Iconos de launcher
│           ├── values/                   Colores y tema XML
│           └── xml/                      file_paths.xml (FileProvider de cámara)
├── build.gradle.kts              Plugins y versiones del proyecto
├── settings.gradle.kts           Nombre del proyecto y módulos
├── gradle/                       Wrapper de Gradle
├── gradlew / gradlew.bat         Scripts de Gradle (Linux/macOS y Windows)
├── gradle.properties             Propiedades de build
└── README.md
```

## Cómo ejecutarlo

1. Clonar el repositorio e importar la carpeta raíz en **Android Studio** (Koala o más reciente).
2. Esperar a que termine el **Gradle Sync**. Android Studio generará automáticamente `local.properties` con la ruta del SDK.
3. Verificar que el SDK de Android 14 (API 34) esté instalado.
4. Ejecutar en un emulador o dispositivo físico con **Android 7.0+** (`Run ▶`).
5. Al abrir la app, conceder los permisos de **cámara** y **galería** cuando se soliciten.

También se puede compilar desde terminal:

```bash
./gradlew assembleDebug        # Linux / macOS
gradlew.bat assembleDebug      # Windows
```

El APK resultante queda en `app/build/outputs/apk/debug/`.

## Archivos que no se suben al repositorio

El archivo `.gitignore` excluye lo que Android Studio regenera automáticamente: `.gradle/`, `.idea/`, `.kotlin/`, `build/`, `app/build/`, `local.properties`, `*.iml` y archivos del sistema. Con esto el repositorio queda ligero y reproducible con solo hacer Sync.
