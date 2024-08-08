package com.ivancea.MTGRules

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ivancea.MTGRules.constants.Actions
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.constants.FirebaseConfig
import com.ivancea.MTGRules.model.HistoryItem
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.VisibleRules
import com.ivancea.MTGRules.presentation.MainViewModel
import com.ivancea.MTGRules.presentation.main.components.main.MainComponent
import com.ivancea.MTGRules.services.ChangelogService
import com.ivancea.MTGRules.services.PermissionsRequesterService
import com.ivancea.MTGRules.services.RulesComparisonService
import com.ivancea.MTGRules.services.RulesService
import com.ivancea.MTGRules.services.StorageService
import com.ivancea.MTGRules.utils.IntentSender
import com.ivancea.MTGRules.utils.RuleUtils
import com.ivancea.MTGRules.utils.RuleUtils.getRuleAndParents
import com.ivancea.MTGRules.utils.RuleUtils.getRuleAndSubsections
import com.ivancea.MTGRules.utils.RulesSearchUtils.search
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Locale
import java.util.Random
import java.util.stream.Collectors
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @JvmField
    @Inject
    var storageService: StorageService? = null

    @JvmField
    @Inject
    var rulesService: RulesService? = null

    @JvmField
    @Inject
    var rulesComparisonService: RulesComparisonService? = null

    @JvmField
    @Inject
    var changelogService: ChangelogService? = null

    @JvmField
    @Inject
    var permissionsRequesterService: PermissionsRequesterService? = null

    private var viewModel: MainViewModel? = null
    private var ttsOk: Boolean? = null
    private var tts: TextToSpeech? = null

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (!popHistoryItem()) {
                finish()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        configureFirebaseRemoteConfig()

        tts = TextToSpeech(this) { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                ttsOk = true
                tts!!.language = Locale.ENGLISH
            } else {
                ttsOk = false
            }
        }

        // Save the last rules source
        val currentRulesSource = rulesService!!.latestRulesSource.date
        storageService!!.lastRulesSource = currentRulesSource

        // Set theme
        val useLightTheme = storageService!!.useLightTheme
        setTheme(useLightTheme)

        // Load ViewModel
        val showSymbols = storageService!!.showSymbols
        viewModel!!.showSymbols.value = showSymbols
        val showAds = storageService!!.showAds
        viewModel!!.showAds.value = showAds

        if (viewModel!!.currentRules.value.isEmpty()) {
            val rulesSource = rulesService!!.latestRulesSource
            viewModel!!.useRules(rulesSource)
        }

        setContent {
            MainComponent()
        }

        permissionsRequesterService!!.requestNotifications()

        changelogService!!.notifyChangelog()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        // Handle initial intent
        handleIntent(intent)
    }

    private fun configureFirebaseRemoteConfig() {
        Firebase.remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        )

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                viewModel!!.showAds.value = storageService!!.showAds
                viewModel!!.bannerAdUnitId.value = FirebaseConfig.getBannerAdUnitId()
            }

            viewModel!!.configLoaded.value = true
        }
    }

    override fun onStop() {
        if (tts != null) {
            tts!!.stop()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null || intent.action == null) {
            return
        }
        val addToHistory = !intent.getBooleanExtra(Actions.IGNORE_HISTORY, false)
        when (intent.action) {
            Actions.ACTION_LOAD_RULES_SOURCE -> {
                val rulesSourceDateString = intent.getStringExtra(Actions.DATA)!!
                val rulesSourceDate = LocalDate.parse(rulesSourceDateString)

                val rulesSource = rulesService!!.rulesSources.find { it.date == rulesSourceDate }

                viewModel!!.useRules(rulesSource!!)
                viewModel!!.logEvent(Events.CHANGE_RULES)
            }

            Intent.ACTION_SEARCH -> {
                val searchString = intent.getStringExtra(SearchManager.QUERY)
                val rootRule = intent.getStringExtra(Actions.ROOT_RULE)
                searchRules(searchString, rootRule)
                if (addToHistory) {
                    pushHistoryItem(
                        HistoryItem(
                            HistoryItem.Type.Search,
                            arrayOf(searchString, rootRule)
                        )
                    )
                }
                viewModel!!.logEvent(Events.SEARCH_RULES)
            }

            Actions.ACTION_READ -> {
                if (ttsOk == null) {
                    Toast.makeText(this, R.string.toast_tts_not_ready, Toast.LENGTH_SHORT).show()
                } else if (!ttsOk!!) {
                    Toast.makeText(this, R.string.toast_tts_not_working, Toast.LENGTH_SHORT).show()
                } else {
                    tts!!.speak(
                        intent.getStringExtra(Actions.DATA),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "rules"
                    )
                    viewModel!!.logEvent(Events.READ_RULE)
                }
            }

            Actions.ACTION_NAVIGATE_RULE -> {
                val title = intent.getStringExtra(Actions.DATA)
                if (title!!.isEmpty()) {
                    viewModel!!.visibleRules.value = VisibleRules.Rules(viewModel!!.currentRules.value)
                    viewModel!!.selectedRuleTitle.value = null
                    viewModel!!.searchText.value = null
                    if (addToHistory) {
                        pushHistoryItem(HistoryItem(HistoryItem.Type.Rule, title))
                    }
                } else if (viewModel!!.selectedRuleTitle.value != title) {
                    val rules = getRuleAndParents(
                        viewModel!!.currentRules.value,
                        title
                    ).collect(Collectors.toCollection { ArrayList() })
                    if (rules.isNotEmpty()) {
                        val rule = rules[rules.size - 1]
                        if (rule.subRules.isEmpty()) {
                            rules.removeAt(rules.size - 1)
                        }
                        rules.addAll(rules[rules.size - 1].subRules)
                        viewModel!!.visibleRules.value = VisibleRules.Rules(rules)
                        if (addToHistory) {
                            pushHistoryItem(
                                HistoryItem(
                                    HistoryItem.Type.Rule,
                                    title
                                )
                            )
                        }
                    }
                    viewModel!!.selectedRuleTitle.value = title
                    viewModel!!.searchText.value = null
                }
            }

            Actions.ACTION_RANDOM_RULE -> {
                val rules = viewModel!!.currentRules.value
                val allRules = rules.stream()
                    .flatMap { obj: Rule -> RuleUtils.flatten(obj) }
                    .filter { r: Rule -> r.subRules.isEmpty() }
                    .collect(Collectors.toList())
                val random = Random()
                val seed = intent.getIntExtra(Actions.DATA, random.nextInt(Int.MAX_VALUE))
                random.setSeed(seed.toLong())
                val rulesToSkip = random.nextInt(allRules.size)
                allRules.stream()
                    .skip(rulesToSkip.toLong())
                    .findFirst()
                    .ifPresent { (title): Rule ->
                        val newVisibleRules = getRuleAndParents(
                            viewModel!!.currentRules.value,
                            title
                        ).collect(Collectors.toList())
                        viewModel!!.visibleRules.value = VisibleRules.Rules(newVisibleRules)
                        viewModel!!.selectedRuleTitle.value = null
                        viewModel!!.searchText.value = null
                        if (addToHistory) {
                            pushHistoryItem(HistoryItem(HistoryItem.Type.Random, seed))
                        }
                    }
                viewModel!!.logEvent(Events.RANDOM_RULE)
            }

            Actions.ACTION_CHANGE_THEME -> {
                val newUseLightTheme = !storageService!!.useLightTheme
                setTheme(newUseLightTheme)
                storageService!!.useLightTheme = newUseLightTheme
                recreate()
            }

            Actions.ACTION_TOGGLE_SYMBOLS -> {
                val newShowSymbols = !storageService!!.showSymbols
                viewModel!!.showSymbols.value = newShowSymbols
                storageService!!.showSymbols = newShowSymbols
            }

            Actions.ACTION_TOGGLE_ADS -> {
                val newShowAds = !storageService!!.showAds
                viewModel!!.showAds.value = newShowAds
                storageService!!.showAds = newShowAds

                val bundle = Bundle()
                bundle.putBoolean(FirebaseAnalytics.Param.VALUE, newShowAds)
                viewModel!!.logEvent(Events.TOGGLE_ADS, bundle)
            }
        }
    }

    private fun pushHistoryItem(historyItem: HistoryItem) {
        viewModel!!.pushHistoryItem(historyItem)

        onBackPressedCallback.isEnabled = isLastHistoryItemNavigation;
    }

    private fun popHistoryItem(): Boolean {
        if (viewModel!!.history.value.isEmpty()) {
            return false
        }

        val newHistory = java.util.ArrayList(
            viewModel!!.history.value
        )
        newHistory.removeAt(newHistory.size - 1)
        while (newHistory.isNotEmpty() && newHistory[newHistory.size - 1].type == HistoryItem.Type.Ignored) {
            newHistory.removeAt(newHistory.size - 1)
        }
        if (newHistory.isEmpty()) {
            return false
        }
        val (type, value) = newHistory[newHistory.size - 1]
        viewModel!!.history.value = newHistory
        when (type) {
            HistoryItem.Type.Rule -> {
                IntentSender.openRule(this, value as String, true)
            }

            HistoryItem.Type.Search -> {
                val values = value as Array<String>
                val searchText = values[0]
                val rootRule = if (values.size == 2) values[1] else null
                IntentSender.openSearch(this, searchText, rootRule, true)
            }

            HistoryItem.Type.Random -> {
                IntentSender.openRandomRule(this, value as Int, true)
            }

            HistoryItem.Type.Ignored -> {
            }
        }
        return true
    }

    /**
     * Checks if there is no more than 1 navigation item (The current one) in the history.
     */
    private val isLastHistoryItemNavigation: Boolean
        get() {
            val history = viewModel!!.history.value

            var foundEntry = false
            for (entry in history) {
                if (entry.type == HistoryItem.Type.Ignored) {
                    if (foundEntry) {
                        return false
                    }
                    foundEntry = true
                }
            }

            return true
        }

    private fun setTheme(useLightTheme: Boolean) {
        viewModel!!.darkTheme.value = !useLightTheme
    }

    private fun searchRules(searchText: String?, rootRule: String?) {
        val rules = viewModel!!.currentRules.value
        val rulesToSearch = if (rootRule == null) rules else getRuleAndSubsections(rules, rootRule)
        val filteredRules = search(
            searchText!!, rulesToSearch
        )
        viewModel!!.visibleRules.value = VisibleRules.Rules(filteredRules)
        viewModel!!.selectedRuleTitle.value = null
        viewModel!!.searchText.value = searchText
    }
}