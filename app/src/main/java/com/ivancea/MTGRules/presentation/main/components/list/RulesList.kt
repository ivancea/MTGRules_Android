package com.ivancea.MTGRules.presentation.main.components.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.constants.Symbols
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesSource
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.LocalDate


@Composable
@ExperimentalFoundationApi
fun RulesList(
    rulesSource: RulesSource?,
    rules: List<Rule>,
    currentRules: List<Rule>,
    scrollToRule: String?,
    searchText: String?,
    showSymbols: Boolean,
) {
    val context = LocalContext.current
    val glossaryTermsPatterns = remember(currentRules) {
        makeGlossaryTermsPatterns(currentRules)
    }
    val searchTextPattern = remember(searchText) {
        if (searchText == null) {
            null
        } else {
            makeSearchTextPattern(searchText)
        }
    }
    val lineHeight = MaterialTheme.typography.body1.lineHeight
    val textInlineContent =
        remember(rulesSource, context, lineHeight) { Symbols.makeSymbolsMap(rulesSource, context, lineHeight) }
    val listState = remember(rules, scrollToRule) {
        if (scrollToRule != null) {
            val firstRuleOffset = rules.indexOfFirst { it.title == scrollToRule }

            if (firstRuleOffset == -1) {
                LazyListState()
            } else {
                LazyListState(
                    firstRuleOffset,
                    -10
                )
            }
        } else {
            LazyListState()
        }
    }

    if (rules.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.list_no_items),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        items(items = rules, key = { it.title }) { item ->
            RulesListItem(
                rule = item,
                isNavigatedRule = item.title == scrollToRule,
                glossaryTermsPatterns = glossaryTermsPatterns,
                searchTextPattern = searchTextPattern,
                showSymbols = showSymbols,
                textInlineContent = textInlineContent,
            )
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Preview() {
    RulesList(
        rulesSource = RulesSource(
            URI("https://test.com"),
            LocalDate.of(2024, 1, 1),
            StandardCharsets.UTF_8,
        ),
        rules = listOf(
            Rule("1.", "Block"),
            Rule("100.", "Parent rule"),
            Rule("100.1", "First rule")
        ),
        currentRules = listOf(),
        scrollToRule = null,
        searchText = "rule",
        showSymbols = true
    )
}