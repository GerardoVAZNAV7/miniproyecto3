package com.example.miniproyecto03.util

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object FotoUtils {

    // ---------- Permisos ----------
    fun permisoGaleria(): String =
        if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    fun permisosIniciales(): Array<String> =
        arrayOf(Manifest.permission.CAMERA, permisoGaleria())

    // ---------- Archivos de fotos ----------
    private fun carpetaFotos(context: Context): File =
        File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }

    fun crearArchivoFoto(context: Context): File =
        File(carpetaFotos(context), "IMG_${System.currentTimeMillis()}.jpg")

    fun uriParaCamara(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    // Copia la imagen elegida en la galería al almacenamiento de la app para que no se pierda
    fun copiarDesdeGaleria(context: Context, uri: Uri): String? {
        return try {
            val destino = crearArchivoFoto(context)
            val entrada = context.contentResolver.openInputStream(uri) ?: return null
            entrada.use { input ->
                destino.outputStream().use { salida -> input.copyTo(salida) }
            }
            Uri.fromFile(destino).toString()
        } catch (e: Exception) {
            null
        }
    }

    fun eliminarArchivo(fotoUri: String) {
        try {
            val uri = Uri.parse(fotoUri)
            if (uri.scheme == "file") uri.path?.let { File(it).delete() }
        } catch (_: Exception) {
        }
    }

    // ---------- Fechas ----------
    fun formatearFecha(millis: Long): String =
        SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-MX")).format(Date(millis))

    // El DatePicker trabaja en UTC; estas funciones evitan que la fecha se corra un día
    fun localAUtc(millis: Long): Long {
        val local = Calendar.getInstance().apply { timeInMillis = millis }
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }
        return utc.timeInMillis
    }

    fun utcALocal(millis: Long): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
        val local = Calendar.getInstance().apply {
            clear()
            set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH), 12, 0, 0)
        }
        return local.timeInMillis
    }
}
