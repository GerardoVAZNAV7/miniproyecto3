package com.example.miniproyecto03.presentation

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.miniproyecto03.util.FotoUtils
import kotlinx.coroutines.delay

@Composable
fun AppRaiz(viewModel: PublicacionViewModel = viewModel()) {
    val context = LocalContext.current
    var mostrarSplash by rememberSaveable { mutableStateOf(true) }

    // Solicitud de permisos de cámara y galería al abrir la app
    val lanzadorPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        delay(1800)
        mostrarSplash = false
        val faltantes = FotoUtils.permisosIniciales().filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (faltantes.isNotEmpty()) lanzadorPermisos.launch(faltantes.toTypedArray())
    }

    if (mostrarSplash) {
        SplashScreen()
        return
    }

    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val publicaciones by viewModel.publicaciones.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (rutaActual != Rutas.NUEVA) {
                BarraNavegacion(
                    rutaActual = rutaActual,
                    onIr = { ruta ->
                        if (ruta == Rutas.NUEVA) {
                            navController.navigate(Rutas.NUEVA) { launchSingleTop = true }
                        } else {
                            navController.navigate(ruta) {
                                popUpTo(Rutas.FEED) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.FEED,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
        ) {
            composable(Rutas.FEED) {
                FeedScreen(publicaciones = publicaciones)
            }
            composable(Rutas.NUEVA) {
                NuevaPublicacionScreen(
                    onPublicar = { titulo, descripcion, foto, fecha ->
                        viewModel.guardar(titulo, descripcion, foto, fecha)
                        navController.navigate(Rutas.FEED) {
                            popUpTo(Rutas.FEED) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onCancelar = { navController.popBackStack() }
                )
            }
            composable(Rutas.PERFIL) {
                PerfilScreen(
                    publicaciones = publicaciones,
                    onEliminar = { viewModel.eliminar(it) }
                )
            }
        }
    }
}
