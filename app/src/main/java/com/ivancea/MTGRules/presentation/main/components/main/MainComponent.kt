package com.ivancea.MTGRules.presentation.main.components.main

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.model.VisibleRules
import com.ivancea.MTGRules.presentation.MainViewModel
import com.ivancea.MTGRules.presentation.main.components.about.AboutDialog
import com.ivancea.MTGRules.presentation.main.components.ads.AdBanner
import com.ivancea.MTGRules.presentation.main.components.list.RulesList
import com.ivancea.MTGRules.presentation.main.components.list.diff.DiffList
import com.ivancea.MTGRules.presentation.theme.TodoListTheme

@Composable
@ExperimentalFoundationApi
fun MainComponent(
    viewModel: MainViewModel = hiltViewModel()
) {
    val darkTheme = viewModel.darkTheme.collectAsState().value
    val currentRulesSource = viewModel.currentRulesSource.collectAsState().value
    val currentRules = viewModel.currentRules.collectAsState().value
    val visibleRules = viewModel.visibleRules.collectAsState().value
    val selectedRule = viewModel.selectedRuleTitle.collectAsState().value
    val searchText = viewModel.searchText.collectAsState().value
    val showSymbols = viewModel.showSymbols.collectAsState().value
    val configLoaded = viewModel.configLoaded.collectAsState().value
    val showAds = viewModel.showAds.collectAsState().value
    val bannerAdUnitId = viewModel.bannerAdUnitId.collectAsState().value
    val showAboutDialog = viewModel.showAboutDialog.collectAsState().value

    TodoListTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    TopBarMenu(
                        darkTheme = darkTheme,
                        showAds = showAds,
                        rulesSource = currentRulesSource,
                        onShowAbout = { viewModel.showAboutDialog.value = true },
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    Box(modifier = Modifier.weight(1f, true)) {
                        when (visibleRules) {
                            is VisibleRules.Empty -> {}
                            is VisibleRules.Rules -> {
                                RulesList(
                                    rulesSource = currentRulesSource,
                                    rules = visibleRules.rules,
                                    currentRules = currentRules,
                                    scrollToRule = selectedRule,
                                    searchText = searchText,
                                    showSymbols = showSymbols,
                                )
                            }
                            is VisibleRules.Diff -> {
                                DiffList(
                                    diff = visibleRules.diff,
                                    currentRules = currentRules,
                                    scrollToRule = selectedRule,
                                    searchText = searchText,
                                    showSymbols = showSymbols,
                                )
                            }
                        }
                    }

                    if (configLoaded) {
                        AdBanner(
                            showAds = showAds,
                            bannerAdUnitId = bannerAdUnitId,
                            onAdError = {
                                val bundle = Bundle()
                                bundle.putInt(FirebaseAnalytics.Param.VALUE, it.code)
                                viewModel.logEvent(Events.AD_ERROR, bundle)
                            }
                        )
                    }
                }
            }

            if (showAboutDialog) {
                AboutDialog(onClose = { viewModel.showAboutDialog.value = false })
            }
        }
    }
}