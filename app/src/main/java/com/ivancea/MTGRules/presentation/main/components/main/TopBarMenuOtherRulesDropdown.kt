package com.ivancea.MTGRules.presentation.main.components.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.utils.IntentSender

data class ExternalLink(
    @StringRes val stringResourceId: Int,
    val link: String? = null,
    val onClick: ((context: Context) -> Unit)? = null,
    val icon: ImageVector = Icons.AutoMirrored.Default.OpenInNew
)

// List of external links
val externalLinks = listOf(
    // Magic tournament rules (MTR)
    ExternalLink(
        R.string.menu_other_rules_magic_tournament_rules,
        "https://media.wizards.com/ContentResources/WPN/MTG_MTR_2024_May13.pdf"
    ),
    // Infraction procedure guide (IPG)
    ExternalLink(
        R.string.menu_other_rules_infraction_procedure_guide,
        "https://media.wizards.com/ContentResources/WPN/MTG_MTR_2024_May13.pdf"
    ),
    // Other documents
    ExternalLink(
        R.string.menu_other_rules_other_documents,
        "https://wpn.wizards.com/en/rules-documents"
    ),
    // About
    ExternalLink(
        R.string.menu_other_rules_about,
        onClick = { context ->
            if (context !is Activity) {
                return@ExternalLink
            }

            AlertDialog.Builder(context)
                .setMessage(R.string.alert_about_other_rules_message)
                .show()
        },
        icon = Icons.AutoMirrored.Default.Help

    )
)

@Composable
fun TopBarMenuOtherRulesDropdown(
    onClose: () -> Unit
) {
    val context = LocalContext.current

    DropdownMenu(
        expanded = true,
        onDismissRequest = onClose
    ) {
        for (externalLink in externalLinks) {
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(externalLink.icon, null)
                        Text(stringResource(externalLink.stringResourceId))
                    }
                },
                onClick = {
                    onClose()
                    externalLink.onClick?.let {
                        it(context)
                    }
                    externalLink.link?.let {
                        IntentSender.openExternalUri(context, it)
                    }
                }
            )
        }
    }
}