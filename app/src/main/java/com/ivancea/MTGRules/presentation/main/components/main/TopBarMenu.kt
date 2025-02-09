package com.ivancea.MTGRules.presentation.main.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.utils.IntentSender
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            // Home
            IconButton(onClick = { IntentSender.openRule(context, "", false) }) {
                Icon(
                    Icons.Default.Home,
                    stringResource(R.string.menu_home),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Search
            TopBarSearchBar()

            // More dropdown
            IconButton(onClick = { showDropdown = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    stringResource(R.string.menu_more),
                    tint = MaterialTheme.colorScheme.primary
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