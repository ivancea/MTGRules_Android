package com.ivancea.MTGRules.presentation.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun BaseDialog(onDismissed: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = { onDismissed() }) {
        Surface(
            modifier = Modifier
                .heightIn(max = (LocalConfiguration.current.screenHeightDp.dp - (16.dp * 2)))
                .widthIn(max = (LocalConfiguration.current.screenWidthDp.dp - (8.dp * 2)))
                .wrapContentSize()
        ) {
            Box(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colors.onSurface)
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}