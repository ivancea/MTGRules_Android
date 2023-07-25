package com.ivancea.MTGRules

import android.app.AlertDialog
import android.app.SearchManager
import android.content.DialogInterface
import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.ivancea.MTGRules.constants.Actions
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.model.HistoryItem
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.presentation.main.components.main.MainComponent
import com.ivancea.MTGRules.services.PermissionsRequesterService
import com.ivancea.MTGRules.services.RulesComparisonService
import com.ivancea.MTGRules.services.RulesService
import com.ivancea.MTGRules.services.StorageService
import com.ivancea.MTGRules.presentation.MainViewModel
import com.ivancea.MTGRules.utils.IntentSender.changeTheme
import com.ivancea.MTGRules.utils.IntentSender.openRandomRule
import com.ivancea.MTGRules.utils.IntentSender.openRule
import com.ivancea.MTGRules.utils.IntentSender.openSearch
import com.ivancea.MTGRules.utils.IntentSender.toggleSymbols
import com.ivancea.MTGRules.utils.RuleUtils
import com.ivancea.MTGRules.utils.RuleUtils.getRuleAndParents
import com.ivancea.MTGRules.utils.RuleUtils.getRuleAndSubsections
import com.ivancea.MTGRules.utils.RulesSearchUtils.search
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Collections
import java.util.Locale
import java.util.Random
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import java.util.stream.Collectors
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
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
    var permissionsRequesterService: PermissionsRequesterService? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var viewModel: MainViewModel? = null
    private var ttsOk: Boolean? = null
    private var tts: TextToSpeech? = null

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set variables
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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
        storageService!!.setLastRulesSource(currentRulesSource)

        // Set theme
        val useLightTheme = storageService!!.useLightTheme
        setTheme(useLightTheme)

        // Load ViewModel
        val showSymbols = storageService!!.showSymbols
        viewModel!!.showSymbols.value = showSymbols
        if (viewModel!!.currentRules.value.isEmpty()) {
            val rulesSource = rulesService!!.latestRulesSource
            useRules(rulesSource)
        }

        setContent {
            MainComponent()
        }
        permissionsRequesterService!!.requestNotifications()

        // Configure action bar
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setLogo(R.drawable.ic_launcher_foreground)
            actionBar.setTitle(R.string.app_name)
            // Collect in coroutine

            lifecycleScope.launch {
                viewModel!!.actionbarSubtitle.flowWithLifecycle(lifecycle)
                    .collect(actionBar::setSubtitle)
            }
        }

        // Handle initial intent
        handleIntent(intent)
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
                logEvent(Events.SEARCH_RULES)
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
                    logEvent(Events.READ_RULE)
                }
            }

            Actions.ACTION_NAVIGATE_RULE -> {
                val title = intent.getStringExtra(Actions.DATA)
                if (title!!.isEmpty()) {
                    viewModel!!.visibleRules.value = viewModel!!.currentRules.value
                    viewModel!!.selectedRuleTitle.value = null
                    viewModel!!.searchText.value = null
                    if (addToHistory) {
                        pushHistoryItem(HistoryItem(HistoryItem.Type.Rule, title))
                    }
                } else {
                    val existingRule = viewModel!!.visibleRules.value.stream()
                        .filter { (title1): Rule -> title1 == title }
                        .findAny().orElse(null)
                    if (existingRule == null || !existingRule.subRules.isEmpty() || !isLastHistoryItemNavigation) {
                        val rules: MutableList<Rule> = getRuleAndParents(
                            viewModel!!.currentRules.value,
                            title
                        ).collect(
                            Collectors.toCollection(
                                Supplier { ArrayList() })
                        )
                        if (!rules.isEmpty()) {
                            val rule = rules[rules.size - 1]
                            if (rule.subRules.isEmpty()) {
                                rules.removeAt(rules.size - 1)
                            }
                            rules.addAll(rules[rules.size - 1].subRules)
                            viewModel!!.visibleRules.value = rules
                            if (addToHistory) {
                                pushHistoryItem(HistoryItem(HistoryItem.Type.Rule, title))
                            }
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
                        viewModel!!.visibleRules.value = newVisibleRules
                        viewModel!!.selectedRuleTitle.value = null
                        viewModel!!.searchText.value = null
                        if (addToHistory) {
                            pushHistoryItem(HistoryItem(HistoryItem.Type.Random, seed))
                        }
                    }
                logEvent(Events.RANDOM_RULE)
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
        }
    }

    private fun pushHistoryItem(historyItem: HistoryItem) {
        val newHistory = java.util.ArrayList(
            viewModel!!.history.value
        )
        newHistory.add(historyItem)
        viewModel!!.history.value = newHistory
    }

    private fun popHistoryItem(): Boolean {
        val newHistory = java.util.ArrayList(
            viewModel!!.history.value
        )
        if (newHistory.isEmpty()) {
            return false
        }
        newHistory.removeAt(newHistory.size - 1)
        while (!newHistory.isEmpty() && newHistory[newHistory.size - 1].type == HistoryItem.Type.Ignored) {
            newHistory.removeAt(newHistory.size - 1)
        }
        if (newHistory.isEmpty()) {
            return false
        }
        val (type, value) = newHistory[newHistory.size - 1]
        viewModel!!.history.value = newHistory
        when (type) {
            HistoryItem.Type.Rule -> {
                openRule(this, value as String, true)
            }

            HistoryItem.Type.Search -> {
                val values = value as Array<String>
                val searchText = values[0]
                val rootRule = if (values.size == 2) values[1] else null
                openSearch(this, searchText, rootRule, true)
            }

            HistoryItem.Type.Random -> {
                openRandomRule(this, value as Int, true)
            }

            HistoryItem.Type.Ignored -> {
            }
        }
        return true
    }

    private val isLastHistoryItemNavigation: Boolean
        private get() {
            val history = viewModel!!.history.value
            return !history.isEmpty() && history[history.size - 1].type == HistoryItem.Type.Rule
        }

    private fun logEvent(event: String) {
        mFirebaseAnalytics!!.logEvent(event, null)
    }

    private fun setTheme(useLightTheme: Boolean) {
        viewModel!!.darkTheme.value = !useLightTheme

        // TODO: Remove after Jetpack migration
        setTheme(if (useLightTheme) R.style.LightTheme else R.style.DarkTheme)
    }

    private fun searchRules(searchText: String?, rootRule: String?) {
        val rules = viewModel!!.currentRules.value
        val rulesToSearch = if (rootRule == null) rules else getRuleAndSubsections(rules, rootRule)
        val filteredRules = search(
            searchText!!, rulesToSearch
        )
        viewModel!!.visibleRules.value = filteredRules
        viewModel!!.selectedRuleTitle.value = null
        viewModel!!.searchText.value = searchText
    }

    private fun useRules(rulesSource: RulesSource) {
        val rules = rulesService!!.loadRules(rulesSource)
        viewModel!!.currentRules.value = rules
        viewModel!!.visibleRules.value = rules
        viewModel!!.selectedRuleTitle.value = null
        viewModel!!.searchText.value = null
        viewModel!!.history.value = listOf(HistoryItem(HistoryItem.Type.Rule, ""))
        viewModel!!.actionbarSubtitle
            .value = getString(R.string.action_bar_rules) + ": " + rulesSource.date.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView?
        searchView!!.queryHint = getString(R.string.search_hint)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        val columnNames = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val viewIds = intArrayOf(android.R.id.text1)
        val adapter: CursorAdapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_1,
            null,
            columnNames,
            viewIds,
            0
        )
        searchView.suggestionsAdapter = adapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val columns = arrayOf("_id", SearchManager.SUGGEST_COLUMN_TEXT_1)
                val cursor = MatrixCursor(columns)
                val newTextUpperCase = newText.uppercase(Locale.getDefault())
                val rules = viewModel!!.currentRules.value
                if (!newText.isEmpty()) {
                    rules[rules.size - 1].subRules.stream()
                        .map(Rule::title)
                        .filter { r: String ->
                            r.uppercase(Locale.getDefault()).contains(newTextUpperCase)
                        }
                        .forEach { r: String -> cursor.newRow().add(r.hashCode()).add(r) }
                }
                adapter.changeCursor(cursor)
                adapter.notifyDataSetChanged()
                return true
            }
        })
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                adapter.cursor.moveToPosition(position)
                val title = adapter.cursor.getString(1)
                searchView.clearFocus()
                openRule(this@MainActivity, title, false)
                return true
            }
        })
        menu.findItem(R.id.home).setOnMenuItemClickListener { view: MenuItem? ->
            openRule(this, "", false)
            true
        }
        menu.findItem(R.id.randomRule).setOnMenuItemClickListener { view: MenuItem? ->
            openRandomRule(this, null, false)
            true
        }
        menu.findItem(R.id.changeTheme).setOnMenuItemClickListener { view: MenuItem? ->
            changeTheme(this)
            true
        }
        menu.findItem(R.id.toggleSymbols).setOnMenuItemClickListener { view: MenuItem? ->
            toggleSymbols(this)
            true
        }
        menu.findItem(R.id.compareRules).setOnMenuItemClickListener { view: MenuItem? ->
            val formattedRulesSources: List<String?> = rulesService!!.rulesSources.stream()
                .map { (_, date): RulesSource ->
                    date.format(
                        DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.MEDIUM
                        )
                    )
                }
                .collect(
                    Collectors.toCollection(
                        Supplier { ArrayList() })
                )
            Collections.reverse(formattedRulesSources)
            showPicker(R.string.dialog_select_source_rules, formattedRulesSources)
                .thenAccept { selectedSourceIndex: Int ->
                    showPicker(R.string.dialog_select_target_rules, formattedRulesSources)
                        .thenAccept { selectedTargetIndex: Int ->
                            val sourceRulesSource =
                                rulesService!!.rulesSources[rulesService!!.rulesSources.size - selectedSourceIndex - 1]
                            val targetRulesSource =
                                rulesService!!.rulesSources[rulesService!!.rulesSources.size - selectedTargetIndex - 1]
                            val sourceRules = rulesService!!.loadRules(sourceRulesSource)
                            val targetRules = rulesService!!.loadRules(targetRulesSource)
                            val comparedRules =
                                rulesComparisonService!!.compareRules(sourceRules, targetRules)
                            viewModel!!.visibleRules.value = comparedRules
                            viewModel!!.selectedRuleTitle.value = null
                            viewModel!!.searchText.value = null
                            pushHistoryItem(HistoryItem(HistoryItem.Type.Ignored, null))
                            logEvent(Events.COMPARE_RULES)
                        }
                }
            true
        }
        menu.findItem(R.id.changeRules).setOnMenuItemClickListener { view: MenuItem? ->
            val formattedRulesSources = rulesService!!.rulesSources.stream()
                .map { (_, date): RulesSource ->
                    date.format(
                        DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.MEDIUM
                        )
                    )
                }
                .collect(
                    Collectors.toCollection { ArrayList() }
                )
            formattedRulesSources.reverse()
            showPicker(R.string.dialog_select_rules, formattedRulesSources)
                .thenAccept { selectedIndex: Int ->
                    val rulesSource =
                        rulesService!!.rulesSources[rulesService!!.rulesSources.size - selectedIndex - 1]
                    useRules(rulesSource)
                    logEvent(Events.CHANGE_RULES)
                }
            true
        }
        menu.findItem(R.id.about).setOnMenuItemClickListener { view: MenuItem? ->
            viewModel!!.showAboutDialog.value = true
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun showPicker(titleId: Int, pickElement: List<String?>): CompletableFuture<Int> {
        val future = CompletableFuture<Int>()
        AlertDialog.Builder(this)
            .setTitle(titleId)
            .setNegativeButton(R.string.dialog_cancel) { dialog: DialogInterface?, which: Int -> }
            .setItems(
                pickElement.toTypedArray()
            ) { dialog: DialogInterface?, which: Int -> future.complete(which) }
            .show()
        return future
    }

    override fun onBackPressed() {
        if (!popHistoryItem()) {
            super.onBackPressed()
        }
    }
}