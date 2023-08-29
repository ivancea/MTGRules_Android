package com.ivancea.MTGRules.presentation

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.constants.FirebaseConfig
import com.ivancea.MTGRules.model.HistoryItem
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.services.RulesComparisonService
import com.ivancea.MTGRules.services.RulesService
import com.ivancea.MTGRules.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val application: Application,
    val rulesService: RulesService,
    private val rulesComparisonService: RulesComparisonService,
    private val storageService: StorageService
) : ViewModel() {
    private val applicationContext get() = application.applicationContext
    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(applicationContext) }

    val configLoaded = MutableStateFlow(false)
    val currentRulesSource = MutableStateFlow<RulesSource?>(null)
    val currentRules = MutableStateFlow(emptyList<Rule>())
    val visibleRules = MutableStateFlow(emptyList<Rule>())
    val selectedRuleTitle = MutableStateFlow<String?>(null)
    val searchText = MutableStateFlow<String?>(null)
    val history = MutableStateFlow(emptyList<HistoryItem>())

    val showSymbols = MutableStateFlow(true)
    val showAds = MutableStateFlow(storageService.showAds)
    val bannerAdUnitId = MutableStateFlow(FirebaseConfig.getBannerAdUnitId())
    val darkTheme = MutableStateFlow(true)
    val showAboutDialog = MutableStateFlow(false)

    fun useRules(rulesSource: RulesSource) {
        val rules = rulesService.loadRules(rulesSource)
        currentRulesSource.value = rulesSource
        currentRules.value = rules
        visibleRules.value = rules
        selectedRuleTitle.value = null
        searchText.value = null
        history.value = listOf(HistoryItem(HistoryItem.Type.Rule, ""))
    }

    fun compareRules(source: RulesSource, target: RulesSource) {
        val sourceRules = rulesService.loadRules(source)
        val targetRules = rulesService.loadRules(target)
        val comparedRules =
            rulesComparisonService.compareRules(sourceRules, targetRules)
        visibleRules.value = comparedRules
        selectedRuleTitle.value = null
        searchText.value = null
        pushHistoryItem(HistoryItem(HistoryItem.Type.Ignored, null))
        logEvent(Events.COMPARE_RULES)
    }

    fun pushHistoryItem(historyItem: HistoryItem) {
        val newHistory = java.util.ArrayList(
            history.value
        )
        newHistory.add(historyItem)
        history.value = newHistory
    }

    fun logEvent(event: String, bundle: Bundle? = null) {
        firebaseAnalytics.logEvent(event, bundle)
    }
}