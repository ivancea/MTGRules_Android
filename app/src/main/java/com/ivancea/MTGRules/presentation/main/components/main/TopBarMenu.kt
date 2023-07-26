package com.ivancea.MTGRules.presentation.main.components.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiSymbols
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.utils.IntentSender
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TopBarMenu(darkTheme: Boolean, rulesSource: RulesSource?, onShowAbout: () -> Unit) {
    val context = LocalContext.current

    var showDropdown by remember { mutableStateOf(false) }

    val subtitle = stringResource(R.string.action_bar_rules) + ": " + rulesSource?.date?.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    )

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
            SearchBar()

            // More dropdown
            IconButton(onClick = { showDropdown = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    stringResource(R.string.menu_more),
                    tint = MaterialTheme.colors.primary
                )
            }
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                // Random rule
                DropdownMenuItem(onClick = {
                    IntentSender.openRandomRule(context, null, false)
                    showDropdown = false
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
                    /*TODO*/
                    showDropdown = false
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
                    IntentSender.changeTheme(context)
                    showDropdown = false
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
                    IntentSender.toggleSymbols(context)
                    showDropdown = false
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
                    /*TODO*/
                    showDropdown = false
                }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Merge, null)
                        Text(stringResource(R.string.menu_compare_rules))
                    }
                }

                // About
                DropdownMenuItem(onClick = {
                    onShowAbout()
                    showDropdown = false
                }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Help, null)
                        Text(stringResource(R.string.menu_about))
                    }
                }
            }
        }
    )
}

@Composable
private fun SearchBar() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val textFieldFocusRequester = remember { FocusRequester() }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    BackHandler(searchExpanded) {
        searchExpanded = false
    }

    if (!searchExpanded) {
        IconButton(onClick = { searchExpanded = true }) {
            Icon(
                Icons.Default.Search,
                stringResource(R.string.menu_search),
                tint = MaterialTheme.colors.primary
            )
        }
    } else {
        SideEffect {
            textFieldFocusRequester.requestFocus()
        }

        TextField(
            value = searchText,
            singleLine = true,
            onValueChange = {
                searchText = it
            },
            trailingIcon = {
                IconButton(onClick = { searchExpanded = false }) {
                    Icon(
                        Icons.Default.Close,
                        stringResource(R.string.menu_search),
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(textFieldFocusRequester),
            label = {
                Text(
                    stringResource(R.string.menu_search),
                    color = MaterialTheme.colors.primary
                )
            },
            placeholder = {
                Text(
                    stringResource(R.string.search_hint),
                    color = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    IntentSender.openSearch(context, searchText, null, false)
                }
            )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    TopBarMenu(darkTheme = true, rulesSource = null, onShowAbout = {})
}