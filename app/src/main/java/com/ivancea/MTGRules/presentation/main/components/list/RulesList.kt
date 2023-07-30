package com.ivancea.MTGRules.presentation.main.components.list

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.isUnspecified
import androidx.core.content.res.ResourcesCompat
import com.ivancea.MTGRules.constants.Symbols
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.utils.RulesSearchUtils
import java.util.regex.Pattern


@Composable
@ExperimentalFoundationApi
fun RulesList(
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
        remember(context, lineHeight) { makeSymbolsMap(context, lineHeight) }
    val listState = rememberLazyListState()

    LaunchedEffect(rules, scrollToRule) {
        if (scrollToRule != null) {
            val index = rules.indexOfFirst { it.title == scrollToRule }

            if (index != -1) {
                listState.scrollToItem(index, -10)
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

fun makeGlossaryTermsPatterns(currentRules: List<Rule>): List<Pair<Pattern, String>> {
    val patterns = arrayListOf<Pair<Pattern, String>>()

    if (currentRules.isEmpty()) {
        return patterns
    }

    val glossaryRule = currentRules.last()
    val glossaryTerms = glossaryRule.subRules
        .map(Rule::title)
        .sortedByDescending { obj: String -> obj.length }

    for (glossaryTerm in glossaryTerms) {
        val pattern = Pattern.compile(
            "\\b" + makePluralAcceptingGlossaryRegex(Pattern.quote(glossaryTerm)) + "\\b",
            Pattern.CASE_INSENSITIVE
        )

        patterns.add(Pair(pattern, glossaryTerm))
    }

    return patterns
}

private fun makeSearchTextPattern(searchText: String): Pattern {
    val tokens = RulesSearchUtils.tokenize(searchText)
    val regex = tokens.joinToString("|") { s: String -> Pattern.quote(s) }

    return Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
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

/**
 * A map of inline content to be passed to the [Text] composable.
 * It will allow for the use of symbols in the rules.
 */
private fun makeSymbolsMap(context: Context, lineHeight: TextUnit): Map<String, InlineTextContent> {
    return Symbols.drawablesBySymbol.mapValues { (_, resource) ->
        val drawable = ResourcesCompat.getDrawable(context.resources, resource, context.theme)
        val height = if (lineHeight.isUnspecified) {
            TextUnit(16f, TextUnitType.Sp)
        } else {
            lineHeight
        }
        val width =
            height.times(drawable!!.intrinsicWidth.toDouble() / drawable.intrinsicHeight.toDouble())

        InlineTextContent(
            Placeholder(width, height, PlaceholderVerticalAlign.TextCenter)
        ) {
            Image(
                painterResource(resource),
                contentDescription = it,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.secondaryVariant)
                    .fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Preview() {
    RulesList(
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