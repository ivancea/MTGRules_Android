package com.ivancea.MTGRules.presentation.main.components.main

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
fun MainComponent(
    viewModel: MainViewModel = hiltViewModel()
) {
    val rules = viewModel.visibleRules.collectAsState().value

    TodoListTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    RulesList(rules = rules)
                }
            }
        }
    }
}