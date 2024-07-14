package com.ivancea.MTGRules.presentation.main.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiSymbols
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.utils.IntentSender
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TopBarMenu(
    darkTheme: Boolean,
    showAds: Boolean,
    rulesSource: RulesSource?,
    onShowAbout: () -> Unit
) {
    val context = LocalContext.current

    var showDropdown by remember { mutableStateOf(false) }

    val subtitle = stringResource(R.string.action_bar_rules) + ": " + rulesSource?.date?.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    )

    var showChooseRulesDialog by remember { mutableStateOf(false) }
    var showCompareRulesDialog by remember { mutableStateOf(false) }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            // Home
            IconButton(onClick = { IntentSender.openRule(context, "", false) }) {
                Icon(
                    Icons.Default.Home,
                    stringResource(R.string.menu_home),
                    tint = MaterialTheme.colors.primary
                )
            }

            // Search
            TopBarSearchBar()

            // More dropdown
            IconButton(onClick = { showDropdown = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    stringResource(R.string.menu_more),
                    tint = MaterialTheme.colors.primary
                )
            }

            TopBarMenuDropdown(
                showDropdown = showDropdown,
                darkTheme = darkTheme,
                showAds = showAds,
                onShowAbout = onShowAbout,
                onShowChooseRulesDialog = { showChooseRulesDialog = true },
                onShowCompareRulesDialog = { showCompareRulesDialog = true },
                onClose = { showDropdown = false },
            )
        }
    )

    if (showChooseRulesDialog) {
        ChooseRulesDialog(
            onClose = { showChooseRulesDialog = false }
        )
    }

    if (showCompareRulesDialog) {
        CompareRulesDialog(
            onClose = { showCompareRulesDialog = false }
        )
    }
}