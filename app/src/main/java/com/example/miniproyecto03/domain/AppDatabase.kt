package com.example.miniproyecto03.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.miniproyecto03.R
import com.example.miniproyecto03.data.Publicacion
import com.example.miniproyecto03.data.PublicacionDao

@Database(entities = [Publicacion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun publicacionDao(): PublicacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "red_social_uas_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            sembrarPublicaciones(appContext, db)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Publicaciones de ejemplo que se insertan solo la primera vez que se crea la BD
        private fun sembrarPublicaciones(context: Context, db: SupportSQLiteDatabase) {
            val dia = 24L * 60 * 60 * 1000
            val ahora = System.currentTimeMillis()
            val base = "android.resource://${context.packageName}/"

            val ejemplos = listOf(
                Triple("¡Bienvenidos a Red Social UAS!", "Conecta con tu comunidad universitaria: comparte momentos, ideas y proyectos con tus compañeros.", R.drawable.sample_1) to 0L,
                Triple("Entrega del Mini Proyecto 3", "Room, cámara y galería funcionando en Kotlin con Jetpack Compose. ¡Ya casi lo terminamos!", R.drawable.sample_2) to 1L,
                Triple("Tarde de código en la FIM", "Practicando MVVM y persistencia local con el equipo. Café, laptops y mucho Android Studio.", R.drawable.sample_3) to 2L,
                Triple("Jornada Universitaria del Conocimiento", "Compartiendo avances de investigación y proyectos de la facultad con toda la comunidad.", R.drawable.sample_4) to 4L
            )

            ejemplos.forEach { (datos, diasAtras) ->
                db.execSQL(
                    "INSERT INTO publicaciones (titulo, descripcion, fotoUri, fecha) VALUES (?, ?, ?, ?)",
                    arrayOf<Any?>(datos.first, datos.second, base + datos.third, ahora - diasAtras * dia)
                )
            }
        }
    }
}
