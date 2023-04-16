package com.ivancea.MTGRules.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivancea.MTGRules.model.HistoryItem
import com.ivancea.MTGRules.model.Rule

class MainViewModel : ViewModel() {
    val currentRules = MutableLiveData(emptyList<Rule>())
    val visibleRules = MutableLiveData(emptyList<Rule>())
    val selectedRuleTitle = MutableLiveData<String?>(null)
    val searchText = MutableLiveData<String?>(null)
    val history = MutableLiveData(emptyList<HistoryItem>())
    val actionbarSubtitle = MutableLiveData<String?>(null)
    val showSymbols = MutableLiveData(true)
}