package com.example.miniproyecto03.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AzulUAS = Color(0xFF012C4E)
val AzulUASClaro = Color(0xFF0A3E6B)
val AzulAcento = Color(0xFF6CB8FF)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulAcento,
    onPrimary = Color(0xFF00213F),
    primaryContainer = Color(0xFF0F4C81),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9AD0FF),
    onSecondary = Color(0xFF00213F),
    background = AzulUAS,
    onBackground = Color.White,
    surface = AzulUASClaro,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF123F6B),
    onSurfaceVariant = Color(0xFFB8CCE4),
    outline = Color(0xFF6F8CB0)
)

@Composable
fun Miniproyecto03Theme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaOscuro, content = content)
}
