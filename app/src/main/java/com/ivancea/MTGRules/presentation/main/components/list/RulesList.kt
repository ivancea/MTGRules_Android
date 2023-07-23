package com.ivancea.MTGRules.presentation.main.components.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.ivancea.MTGRules.model.Rule
import java.util.regex.Pattern


@Composable
@ExperimentalFoundationApi
fun RulesList(
    rules: List<Rule>,
    currentRules: List<Rule>,
    scrollToRule: String? = null,
    searchText: String? = null
) {
    val glossaryTermsPatterns = remember(currentRules) {
        makeGlossaryTermsPatterns(currentRules)
    }
    val searchTextPattern = remember(searchText) {
        if (searchText == null) {
            null
        } else {
            Pattern.compile(
                "\\b" + makePluralAcceptingGlossaryRegex(Pattern.quote(searchText)) + "\\b",
                Pattern.CASE_INSENSITIVE
            )
        }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(rules, scrollToRule) {
        if (scrollToRule != null) {
            val index = rules.indexOfFirst { it.title == scrollToRule }

            if (index != -1) {
                listState.scrollToItem(index)
            }
        }
    }

    if (rules.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "No items", // stringResource(R.string.list__no_items),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items = rules, key = { it.title }) { item ->
            RulesListItem(
                rule = item,
                glossaryTermsPatterns = glossaryTermsPatterns,
                searchTextPattern = searchTextPattern
            )
        }
    }
}

fun makeGlossaryTermsPatterns(currentRules: List<Rule>): List<Pair<Pattern, String>> {
    val glossaryRule = currentRules.last()
    val glossaryTerms = glossaryRule.subRules
        .map(Rule::title)
        .sortedByDescending { obj: String -> obj.length }

    val patterns = arrayListOf<Pair<Pattern, String>>()

    for (glossaryTerm in glossaryTerms) {
        // TODO: Cache patterns on rule set change
        val pattern = Pattern.compile(
            "\\b" + makePluralAcceptingGlossaryRegex(Pattern.quote(glossaryTerm)) + "\\b",
            Pattern.CASE_INSENSITIVE
        )

        patterns.add(Pair(pattern, glossaryTerm))
    }

    return patterns
}

/**
 * Takes a regex, and returns another regex that also accepts plurals.
 *
 * Note: This method should be replaced with a proper pluralization library, or should use translations.
 *
 * @param glossaryRegex The glossary regex to pluralize
 * @return A regex accepting plurals
 */
private fun makePluralAcceptingGlossaryRegex(glossaryRegex: String): String {
    return "$glossaryRegex(?:s|es)?"
}