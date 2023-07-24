package com.ivancea.MTGRules.presentation.main.components.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivancea.MTGRules.presentation.main.components.list.RulesList
import com.ivancea.MTGRules.presentation.theme.TodoListTheme
import com.ivancea.MTGRules.ui.main.MainViewModel

@Composable
@ExperimentalFoundationApi
fun MainComponent(
    viewModel: MainViewModel = hiltViewModel()
) {
    val darkTheme = viewModel.darkTheme.collectAsState().value
    val visibleRules = viewModel.visibleRules.collectAsState().value
    val currentRules = viewModel.visibleRules.collectAsState().value
    val selectedRule = viewModel.selectedRuleTitle.collectAsState().value
    val searchText = viewModel.searchText.collectAsState().value
    val showSymbols = viewModel.showSymbols.collectAsState().value

    TodoListTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    RulesList(
                        rules = visibleRules,
                        currentRules = currentRules,
                        scrollToRule = selectedRule,
                        searchText = searchText,
                        showSymbols = showSymbols,
                    )
                }
            }
        }
    }
}