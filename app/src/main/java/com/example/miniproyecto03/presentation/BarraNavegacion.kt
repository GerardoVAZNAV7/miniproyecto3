package com.example.miniproyecto03.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Rutas {
    const val FEED = "feed"
    const val NUEVA = "nueva"
    const val PERFIL = "perfil"
}

private val FondoBarra = Color(0xFF011B33)

// Barra inferior estilo TikTok: Inicio | + (nueva publicación) | Perfil
@Composable
fun BarraNavegacion(rutaActual: String?, onIr: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoBarra)
            .navigationBarsPadding()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ItemBarra(
            etiqueta = "Inicio",
            iconoActivo = Icons.Filled.Home,
            iconoInactivo = Icons.Outlined.Home,
            seleccionado = rutaActual == Rutas.FEED,
            onClick = { onIr(Rutas.FEED) }
        )
        BotonMas(onClick = { onIr(Rutas.NUEVA) })
        ItemBarra(
            etiqueta = "Perfil",
            iconoActivo = Icons.Filled.Person,
            iconoInactivo = Icons.Outlined.Person,
            seleccionado = rutaActual == Rutas.PERFIL,
            onClick = { onIr(Rutas.PERFIL) }
        )
    }
}

@Composable
private fun RowScope.ItemBarra(
    etiqueta: String,
    iconoActivo: ImageVector,
    iconoInactivo: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val color = if (seleccionado) Color.White else Color(0xFF8FA6BF)
    Column(
        modifier = Modifier
            .weight(1f)
            .height(60.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (seleccionado) iconoActivo else iconoInactivo,
            contentDescription = etiqueta,
            tint = color,
            modifier = Modifier.size(26.dp)
        )
        Text(text = etiqueta, color = color, fontSize = 11.sp)
    }
}

// Botón central con efecto de doble color como el de TikTok
@Composable
private fun BotonMas(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .offset(x = (-5).dp)
                .size(width = 44.dp, height = 32.dp)
                .background(Color(0xFF4FA3FF), RoundedCornerShape(10.dp))
        )
        Box(
            Modifier
                .offset(x = 5.dp)
                .size(width = 44.dp, height = 32.dp)
                .background(Color(0xFFB9DDFF), RoundedCornerShape(10.dp))
        )
        Box(
            Modifier
                .size(width = 44.dp, height = 32.dp)
                .background(Color.White, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Nueva publicación",
                tint = Color(0xFF012C4E),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
