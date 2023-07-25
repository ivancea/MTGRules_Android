package com.ivancea.MTGRules.presentation

import androidx.lifecycle.ViewModel
import com.ivancea.MTGRules.model.HistoryItem
import com.ivancea.MTGRules.model.Rule
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    val currentRules = MutableStateFlow(emptyList<Rule>())
    val visibleRules = MutableStateFlow(emptyList<Rule>())
    val selectedRuleTitle = MutableStateFlow<String?>(null)
    val searchText = MutableStateFlow<String?>(null)
    val history = MutableStateFlow(emptyList<HistoryItem>())
    val actionbarSubtitle = MutableStateFlow<String?>(null)
    val showSymbols = MutableStateFlow(true)
    val darkTheme = MutableStateFlow(true)
    val showAboutDialog = MutableStateFlow(false)
}