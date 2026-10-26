package com.example.miniproyecto03.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto03.data.Publicacion
import com.example.miniproyecto03.data.PublicacionRepository
import com.example.miniproyecto03.domain.AppDatabase
import com.example.miniproyecto03.util.FotoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PublicacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PublicacionRepository(
        AppDatabase.getDatabase(application).publicacionDao()
    )

    val publicaciones: StateFlow<List<Publicacion>> = repositorio.publicaciones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun guardar(titulo: String, descripcion: String, fotoUri: String, fecha: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repositorio.insertar(
                Publicacion(titulo = titulo, descripcion = descripcion, fotoUri = fotoUri, fecha = fecha)
            )
        }
    }

    fun eliminar(publicacion: Publicacion) {
        viewModelScope.launch(Dispatchers.IO) {
            repositorio.eliminar(publicacion)
            FotoUtils.eliminarArchivo(publicacion.fotoUri)
        }
    }
}
