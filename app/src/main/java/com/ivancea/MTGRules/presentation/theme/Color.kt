package com.ivancea.MTGRules.presentation.theme

import androidx.compose.ui.graphics.Color

val darkColorPalette = androidx.compose.material.darkColors(
    primary = Color.White,

    // For links
    primaryVariant = Color.Gray,

    // For search text highlight
    secondary = Color.Yellow.copy(alpha = 0.4f),

    // For symbols background
    secondaryVariant = Color.Gray.copy(alpha = 0.4f),

    background = Color.Black,
)

val lightColorPalette = androidx.compose.material.lightColors(
    primary = Color.Black,

    // For links
    primaryVariant = Color.Gray,

    // For search text highlight
    secondary = Color.Yellow.copy(alpha = 0.4f),

    // For symbols background
    secondaryVariant = Color.Gray.copy(alpha = 0.4f),
)