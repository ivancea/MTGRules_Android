package com.ivancea.MTGRules.presentation.main.components.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.presentation.common.BaseDialog
import com.ivancea.MTGRules.presentation.common.LinkedText

@Composable
fun AboutDialog(onClose: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    BaseDialog(
        onDismissed = { onClose() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(
                    R.string.app_name
                ),
                style = MaterialTheme.typography.displaySmall.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )
            Text(
                stringResource(
                    R.string.versionName
                ),
                style = MaterialTheme.typography.titleLarge.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )

            Divider()

            Text(
                stringResource(
                    R.string.about_description
                ),
                style = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )

            LinkedText(
                stringResource(
                    R.string.about_unofficial
                ),
                style = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
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
                style = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )
            LinkedText(
                stringResource(
                    R.string.about_email
                ),
                style = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                ),
                pattern = android.util.Patterns.EMAIL_ADDRESS,
                onClick = { email -> uriHandler.openUri("mailto:$email") }
            )
            LinkedText(
                stringResource(
                    R.string.about_github
                ),
                style = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                ),
                pattern = android.util.Patterns.WEB_URL,
                onClick = { uri -> uriHandler.openUri(uri) }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AboutDialog(onClose = {})
}