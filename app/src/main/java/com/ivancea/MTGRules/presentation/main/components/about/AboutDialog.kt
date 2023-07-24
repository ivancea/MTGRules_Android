package com.ivancea.MTGRules.presentation.main.components.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.presentation.common.LinkedText

@Composable
fun AboutDialog(onClose: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Dialog(
        onDismissRequest = { onClose() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .border(1.dp, MaterialTheme.colors.onSurface)
                .padding(16.dp)
        ) {
            Text(
                stringResource(
                    R.string.app_name
                ),
                style = MaterialTheme.typography.h3.merge(
                    TextStyle(
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )
            Text(
                stringResource(
                    R.string.versionName
                ),
                style = MaterialTheme.typography.h6.merge(
                    TextStyle(
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )

            Divider()

            Text(
                stringResource(
                    R.string.about_description
                ),
                style = MaterialTheme.typography.body1.merge(
                    TextStyle(
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )

            LinkedText(
                stringResource(
                    R.string.about_unofficial
                ),
                style = MaterialTheme.typography.body1.merge(
                    TextStyle(
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                ),
                pattern = android.util.Patterns.WEB_URL,
                onClick = { url -> uriHandler.openUri(url) }
            )

            Divider()

            Text(
                stringResource(
                    R.string.about_any_request
                ),
                style = MaterialTheme.typography.body1.merge(
                    TextStyle(
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )
            LinkedText(
                stringResource(
                    R.string.about_email
                ),
                style = MaterialTheme.typography.body1.merge(
                    TextStyle(
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                ),
                pattern = android.util.Patterns.EMAIL_ADDRESS,
                onClick = { email -> uriHandler.openUri("mailto:$email") }
            )
        }
    }
}