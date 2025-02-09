package com.ivancea.MTGRules.presentation.theme

import androidx.compose.ui.graphics.Color

val darkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color.White,

    // For links
    secondary = Color.Gray,

    // For search text highlight
    tertiary = Color.Yellow.copy(alpha = 0.4f),

    // For symbols background
    tertiaryContainer = Color.Gray.copy(alpha = 0.4f),

    background = Color.Black,
)

val lightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Color.Black,

    // For links
    secondary = Color.Gray,

    // For search text highlight
    tertiary = Color.Yellow.copy(alpha = 0.4f),

    // For symbols background
    tertiaryContainer = Color.Gray.copy(alpha = 0.4f),
)