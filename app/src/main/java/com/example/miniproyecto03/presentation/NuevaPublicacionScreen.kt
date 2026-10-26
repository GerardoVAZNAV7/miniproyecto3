package com.example.miniproyecto03.presentation

import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.miniproyecto03.util.FotoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPublicacionScreen(
    onPublicar: (titulo: String, descripcion: String, fotoUri: String, fecha: Long) -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var fecha by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var intentoPublicar by remember { mutableStateOf(false) }

    // ---------- Cámara ----------
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val ruta = rutaPendiente
        if (exito && ruta != null) {
            fotoUri = Uri.fromFile(File(ruta)).toString()
        } else if (ruta != null) {
            File(ruta).delete()
        }
        rutaPendiente = null
    }

    fun abrirCamara() {
        val archivo = FotoUtils.crearArchivoFoto(context)
        rutaPendiente = archivo.absolutePath
        camaraLauncher.launch(FotoUtils.uriParaCamara(context, archivo))
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            Toast.makeText(context, "Se necesita el permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show()
        }
    }

    // ---------- Galería ----------
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val ruta = withContext(Dispatchers.IO) { FotoUtils.copiarDesdeGaleria(context, uri) }
                if (ruta != null) {
                    fotoUri = ruta
                } else {
                    Toast.makeText(context, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun abrirGaleria() {
        galeriaLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val permisoGaleriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (!concedido) {
            Toast.makeText(
                context,
                "Permiso de galería denegado: se usará el selector seguro de Android",
                Toast.LENGTH_LONG
            ).show()
        }
        abrirGaleria()
    }

    fun tienePermiso(permiso: String): Boolean =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

    // ---------- Interfaz ----------
    val foto = fotoUri
    val formularioValido = titulo.isNotBlank() && descripcion.isNotBlank() && foto != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancelar) {
                Icon(Icons.Filled.Close, contentDescription = "Cancelar")
            }
            Text(
                text = "Nueva publicación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        // Vista previa de la foto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (foto != null) {
                AsyncImage(
                    model = foto,
                    contentDescription = "Foto de la publicación",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Agrega una foto",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (tienePermiso(android.Manifest.permission.CAMERA)) {
                        abrirCamara()
                    } else {
                        permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Cámara")
            }
            FilledTonalButton(
                onClick = {
                    val permiso = FotoUtils.permisoGaleria()
                    if (tienePermiso(permiso)) {
                        abrirGaleria()
                    } else {
                        permisoGaleriaLauncher.launch(permiso)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Galería")
            }
        }

        if (intentoPublicar && foto == null) {
            Text(
                text = "Agrega una foto para publicar",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { if (it.length <= 60) titulo = it },
            label = { Text("Título") },
            singleLine = true,
            isError = intentoPublicar && titulo.isBlank(),
            supportingText = {
                if (intentoPublicar && titulo.isBlank()) Text("El título es obligatorio")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { if (it.length <= 300) descripcion = it },
            label = { Text("Descripción") },
            minLines = 3,
            maxLines = 5,
            isError = intentoPublicar && descripcion.isBlank(),
            supportingText = {
                if (intentoPublicar && descripcion.isBlank()) Text("La descripción es obligatoria")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Fecha: se genera automáticamente (hoy) y se puede cambiar
        OutlinedButton(
            onClick = { mostrarCalendario = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("  Fecha: ${FotoUtils.formatearFecha(fecha)}")
        }
        Text(
            text = "Se pone la fecha de hoy automáticamente; toca para cambiarla.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                intentoPublicar = true
                if (formularioValido && foto != null) {
                    onPublicar(titulo.trim(), descripcion.trim(), foto, fecha)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Publicar", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
    }

    if (mostrarCalendario) {
        val estadoCalendario = rememberDatePickerState(
            initialSelectedDateMillis = FotoUtils.localAUtc(fecha)
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoCalendario.selectedDateMillis?.let { fecha = FotoUtils.utcALocal(it) }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoCalendario)
        }
    }
}
