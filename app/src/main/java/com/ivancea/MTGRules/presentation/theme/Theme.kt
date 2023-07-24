package com.ivancea.MTGRules.presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TodoListTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = darkColorPalette,
        typography = appTypography,
        content = content
    )
}