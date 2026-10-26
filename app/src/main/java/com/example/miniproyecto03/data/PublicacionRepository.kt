package com.example.miniproyecto03.data

import kotlinx.coroutines.flow.Flow

class PublicacionRepository(private val dao: PublicacionDao) {
    val publicaciones: Flow<List<Publicacion>> = dao.obtenerTodas()

    suspend fun insertar(publicacion: Publicacion) = dao.insertar(publicacion)

    suspend fun eliminar(publicacion: Publicacion) = dao.eliminar(publicacion)
}
