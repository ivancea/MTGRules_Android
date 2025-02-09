package com.ivancea.MTGRules.presentation.main.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiSymbols
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.ivancea.MTGRules.utils.IntentSender

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
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shuffle, null)
                    Text(stringResource(R.string.menu_random_rule))
                }
            },
            onClick = {
                onClose()
                IntentSender.openRandomRule(context, null, false)
            }
        )

        // Change rules
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ClearAll, null)
                    Text(stringResource(R.string.menu_change_rules))
                }
            },
            onClick = {
                onClose()
                onShowChooseRulesDialog()
            }
        )

        // Change theme
        DropdownMenuItem(
            text = {
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
            },
            onClick = {
                onClose()
                IntentSender.changeTheme(context)
            }
        )

        // Show symbols
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.EmojiSymbols, null)
                    Text(stringResource(R.string.menu_toggle_symbols))
                }
            },
            onClick = {
                onClose()
                IntentSender.toggleSymbols(context)
            }
        )

        // Compare rules
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Merge, null)
                    Text(stringResource(R.string.menu_compare_rules))
                }
            },
            onClick = {
                onClose()
                onShowCompareRulesDialog()
            }
        )

        // Activate ads
        DropdownMenuItem(
            text = {
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
            },
            onClick = {
                onClose()
                IntentSender.toggleAds(context)
            }
        )

        // Other rules
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Link, null)
                    Text(stringResource(R.string.menu_other_rules))
                }
            },
            onClick = {
                onClose()
                showOtherRulesDropdown = true
            }
        )

        // About
        DropdownMenuItem(
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Default.Help, null)
                    Text(stringResource(R.string.menu_about))
                }
            },
            onClick = {
                onClose()
                onShowAbout()
            }
        )
    }
}