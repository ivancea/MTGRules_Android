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
fun TopBarMenuDropdown(
    showDropdown: Boolean,
    darkTheme: Boolean,
    showAds: Boolean,
    onShowAbout: () -> Unit,
    onShowChooseRulesDialog: () -> Unit,
    onShowCompareRulesDialog: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    var showOtherRulesDropdown by remember { mutableStateOf(false) }

    if (showOtherRulesDropdown) {
        TopBarMenuOtherRulesDropdown(
            onClose = {
                onClose()
                showOtherRulesDropdown = false
            }
        )
        return
    }

    DropdownMenu(
        expanded = showDropdown,
        onDismissRequest = onClose
    ) {
        // Random rule
        DropdownMenuItem(onClick = {
            onClose()
            IntentSender.openRandomRule(context, null, false)
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Shuffle, null)
                Text(stringResource(R.string.menu_random_rule))
            }
        }

        // Change rules
        DropdownMenuItem(onClick = {
            onClose()
            onShowChooseRulesDialog()
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ClearAll, null)
                Text(stringResource(R.string.menu_change_rules))
            }
        }

        // Change theme
        DropdownMenuItem(onClick = {
            onClose()
            IntentSender.changeTheme(context)
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    null
                )
                Text(stringResource(R.string.menu_change_theme))
            }
        }

        // Show symbols
        DropdownMenuItem(onClick = {
            onClose()
            IntentSender.toggleSymbols(context)
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.EmojiSymbols, null)
                Text(stringResource(R.string.menu_toggle_symbols))
            }
        }

        // Compare rules
        DropdownMenuItem(onClick = {
            onClose()
            onShowCompareRulesDialog()
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Merge, null)
                Text(stringResource(R.string.menu_compare_rules))
            }
        }

        // Activate ads
        DropdownMenuItem(onClick = {
            onClose()
            IntentSender.toggleAds(context)
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showAds) {
                    Icon(Icons.Default.MoneyOff, null)
                    Text(stringResource(R.string.menu_deactivate_ads))
                } else {
                    Icon(Icons.Default.AttachMoney, null)
                    Text(stringResource(R.string.menu_activate_ads))
                }

            }
        }

        // Other rules
        DropdownMenuItem(onClick = {
            onClose()
            showOtherRulesDropdown = true
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Link, null)
                Text(stringResource(R.string.menu_other_rules))
            }
        }

        // About
        DropdownMenuItem(onClick = {
            onClose()
            onShowAbout()
        }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Default.Help, null)
                Text(stringResource(R.string.menu_about))
            }
        }
    }
}