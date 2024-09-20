package com.ivancea.MTGRules.presentation.main.components.list.diff

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
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
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.constants.Symbols
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesDiff
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.presentation.main.components.list.makeGlossaryTermsPatterns
import com.ivancea.MTGRules.presentation.main.components.list.makeSearchTextPattern
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.LocalDate


@Composable
@ExperimentalFoundationApi
fun DiffList(
    diff: RulesDiff,
    currentRules: List<Rule>,
    scrollToRule: String?,
    searchText: String?,
    showSymbols: Boolean,
) {
    /*val rules = remember(diff) {
        diff.changes.map {
            if (it.sourceRule == null && it.targetRule != null) {
                Rule("(+) " + it.targetRule.title, it.targetRule.text)
            } else if (it.sourceRule != null && it.targetRule == null) {
                Rule("(-) " + it.sourceRule.title, it.sourceRule.text)
            } else {
                Rule(
                    "(M) " + it.sourceRule!!.title,
                    """${it.sourceRule.text}

CHANGED TO

${it.targetRule!!.text}"""
                )
            }
        }
    }*/

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
    val sourceTextInlineContent =
        remember(context, lineHeight) { Symbols.makeSymbolsMap(diff.sourceRulesSource, context, lineHeight) }
    val targetTextInlineContent =
        remember(context, lineHeight) { Symbols.makeSymbolsMap(diff.targetRulesSource, context, lineHeight) }
    val listState = remember(diff, scrollToRule) {
        if (scrollToRule != null) {
            val firstRuleOffset = diff.changes.indexOfFirst { it.title == scrollToRule }

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

    if (diff.changes.isEmpty()) {
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
        itemsIndexed(items = diff.changes, key = { index, item -> "${item.title} $index" }) { index, item ->
            if (index != 0) {
                Divider (
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )
            }

            if (item.sourceRule == null && item.targetRule != null) {
                AddedRule(
                    rule = item.targetRule,
                    isNavigatedRule = item.title == scrollToRule,
                    glossaryTermsPatterns = glossaryTermsPatterns,
                    searchTextPattern = searchTextPattern,
                    showSymbols = showSymbols,
                    textInlineContent = targetTextInlineContent,
                )
            } else if (item.sourceRule != null && item.targetRule == null) {
                RemovedRule(
                    rule = item.sourceRule,
                    isNavigatedRule = item.title == scrollToRule,
                    glossaryTermsPatterns = glossaryTermsPatterns,
                    searchTextPattern = searchTextPattern,
                    showSymbols = showSymbols,
                    textInlineContent = sourceTextInlineContent,
                )
            } else {
                ChangedRule(
                    changedRule = item,
                    isNavigatedRule = item.title == scrollToRule,
                    glossaryTermsPatterns = glossaryTermsPatterns,
                    searchTextPattern = searchTextPattern,
                    showSymbols = showSymbols,
                    sourceTextInlineContent = sourceTextInlineContent,
                    targetTextInlineContent = targetTextInlineContent,
                )
            }
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Preview() {
    DiffList(
        diff = RulesDiff(
            sourceRulesSource = RulesSource(
                URI("https://test.com"),
                LocalDate.of(2024, 1, 1),
                StandardCharsets.UTF_8,
            ),
            targetRulesSource = RulesSource(
                URI("https://test.com"),
                LocalDate.of(2024, 6, 30),
                StandardCharsets.UTF_8,
            ),
            changes = listOf(
                RulesDiff.ChangedRule(
                    sourceRule = null,
                    targetRule = Rule("100.5a", "New rule"),
                ),
                RulesDiff.ChangedRule(
                    sourceRule = Rule("100.6c", "Removed rule"),
                    targetRule = null,
                ),
                RulesDiff.ChangedRule(
                    sourceRule = Rule("100.7b", "Changed rule (Old text)"),
                    targetRule = Rule("100.7b", "Changed rule (New text)"),
                )
            )
        ),
        currentRules = listOf(),
        scrollToRule = null,
        searchText = "rule",
        showSymbols = true
    )
}