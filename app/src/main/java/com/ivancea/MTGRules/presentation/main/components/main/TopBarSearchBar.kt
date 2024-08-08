package com.ivancea.MTGRules.presentation.main.components.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.presentation.MainViewModel
import com.ivancea.MTGRules.utils.IntentSender
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopBarSearchBar(viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val textFieldFocusRequester = remember { FocusRequester() }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchValueState by remember { mutableStateOf(TextFieldValue(text = "")) }
    var showSuggestions by remember { mutableStateOf(false) }

    var currentRules = viewModel.currentRules.collectAsState().value

    val trimmedSearchText = searchValueState.text.trim()
    val suggestions = remember(currentRules, trimmedSearchText) {
        if (trimmedSearchText.length < 3) {
            emptyList()
        } else {
            val uppercaseSearchText = trimmedSearchText.uppercase(Locale.getDefault())

            currentRules[currentRules.size - 1].subRules
                .map(Rule::title)
                .filter { r ->
                    r.uppercase(Locale.getDefault()).contains(uppercaseSearchText)
                }
        }
    }

    LaunchedEffect(trimmedSearchText) {
        showSuggestions = suggestions.isNotEmpty()
    }

    LaunchedEffect(searchExpanded) {
        if (searchExpanded) {
            textFieldFocusRequester.requestFocus()
        }
    }

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
        ExposedDropdownMenuBox(
            expanded = showSuggestions,
            onExpandedChange = { showSuggestions = it },
        ) {
            TextField(
                value = searchValueState,
                singleLine = true,
                onValueChange = {
                    searchValueState = it
                },
                trailingIcon = {
                    IconButton(onClick = { searchExpanded = false }) {
                        Icon(
                            Icons.Default.Close,
                            null,
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

                        if (trimmedSearchText.isNotEmpty()) {
                            IntentSender.openSearch(context, trimmedSearchText, null, false)
                        }
                    }
                )
            )

            ExposedDropdownMenu(
                expanded = showSuggestions,
                onDismissRequest = { showSuggestions = false }
            ) {
                suggestions.forEach { suggestion ->
                    key(suggestion) {
                        DropdownMenuItem(onClick = {
                            focusManager.clearFocus()
                            IntentSender.openRule(context, suggestion, false)
                            showSuggestions = false
                        }) {
                            Text(suggestion)
                        }
                    }
                }
            }
        }
    }
}