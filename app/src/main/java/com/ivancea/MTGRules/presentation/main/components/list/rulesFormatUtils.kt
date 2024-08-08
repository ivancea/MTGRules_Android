package com.ivancea.MTGRules.presentation.main.components.list

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.ivancea.MTGRules.constants.Symbols
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.utils.RulesSearchUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

const val NAVIGATE_RULE_ANNOTATION_KEY = "navigate-rule"

private val ruleLinkPattern =
    Pattern.compile("\\b(?<rule>\\d{3})(?:\\.(?<subRule>\\d+)(?<letter>[a-z])?)?\\b")
private val examplePattern = Pattern.compile("^Example:", Pattern.MULTILINE)
private val symbolPattern = Pattern.compile("\\{(?<symbol>[\\w/]+)\\}")

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
fun annotateRuleTitle(
    text: String,
    searchTextPattern: Pattern?
): AnnotatedString {
    val builder = AnnotatedString.Builder(text)

    highlightSearchText(builder, text, searchTextPattern)

    return builder.toAnnotatedString()
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
fun annotateRuleText(
    text: String,
    glossaryTermsPatterns: List<Pair<Pattern, String>>,
    searchTextPattern: Pattern?,
    showSymbols: Boolean
): AnnotatedString {
    val builder = AnnotatedString.Builder(text)

    formatRuleTitleLinks(builder, text)
    formatGlossaryLinks(builder, text, glossaryTermsPatterns)
    highlightSearchText(builder, text, searchTextPattern)
    formatExample(builder, text)

    if (showSymbols) {
        replaceSymbols(builder, text)
    }

    return builder.toAnnotatedString()
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun highlightSearchText(
    builder: AnnotatedString.Builder,
    ruleText: String,
    searchTextPattern: Pattern?
) {
    if (searchTextPattern == null) {
        return
    }

    val searchTextMatcher: Matcher = searchTextPattern.matcher(ruleText)

    while (searchTextMatcher.find()) {
        // Highlight with a background color
        builder.addStyle(
            SpanStyle(background = MaterialTheme.colors.secondary),
            searchTextMatcher.start(),
            searchTextMatcher.end()
        )
    }
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun formatRuleTitleLinks(builder: AnnotatedString.Builder, ruleText: String) {
    val linkMatcher = ruleLinkPattern.matcher(ruleText)

    while (linkMatcher.find()) {
        val ruleTitle: String = normalizeRuleTitle(
            linkMatcher.group("rule"),
            linkMatcher.group("subRule"),
            linkMatcher.group("letter")
        )

        annotateRuleTitleLink(
            builder = builder,
            ruleTitle = ruleTitle,
            start = linkMatcher.start(),
            end = linkMatcher.end()
        )
    }
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun formatGlossaryLinks(
    builder: AnnotatedString.Builder,
    ruleText: String,
    glossaryTermsPatterns: List<Pair<Pattern, String>>
) {
    for ((pattern, glossaryTerm) in glossaryTermsPatterns) {
        val matcher = pattern.matcher(ruleText)

        while (matcher.find()) {
            annotateRuleTitleLink(
                builder = builder,
                ruleTitle = glossaryTerm,
                start = matcher.start(),
                end = matcher.end()
            )
        }
    }
}

private fun formatExample(builder: AnnotatedString.Builder, ruleText: String) {
    val exampleMatcher = examplePattern.matcher(ruleText)

    if (exampleMatcher.find()) {
        builder.addStyle(
            SpanStyle(fontStyle = FontStyle.Italic),
            exampleMatcher.start(),
            ruleText.length
        )
    }
}

private fun replaceSymbols(
    builder: AnnotatedString.Builder,
    ruleText: String
) {
    val symbolMatcher = symbolPattern.matcher(ruleText)

    while (symbolMatcher.find()) {
        val symbol = symbolMatcher.group("symbol")!!

        if (Symbols.drawablesBySymbol.containsKey(symbol)) {
            builder.addStringAnnotation(
                // From InlineTextContent.INLINE_CONTENT_TAG
                "androidx.compose.foundation.text.inlineContent",
                symbol,
                symbolMatcher.start(),
                symbolMatcher.end()
            )
        }
    }
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun annotateRuleTitleLink(
    builder: AnnotatedString.Builder,
    ruleTitle: String,
    start: Int,
    end: Int
) {
    builder.addStringAnnotation(
        NAVIGATE_RULE_ANNOTATION_KEY,
        ruleTitle,
        start,
        end
    )
    builder.addStyle(
        SpanStyle(
            color = MaterialTheme.colors.primaryVariant,
            fontWeight = FontWeight.Bold
        ),
        start,
        end
    )
}

private fun normalizeRuleTitle(
    ruleNumber: String?,
    subRuleNumber: String?,
    subRuleLetter: String?
): String {
    if (subRuleNumber == null) {
        return "$ruleNumber."
    }

    if (subRuleLetter == null) {
        return "$ruleNumber.$subRuleNumber."
    }

    return "$ruleNumber.$subRuleNumber$subRuleLetter"
}


fun makeSearchTextPattern(searchText: String): Pattern {
    val tokens = RulesSearchUtils.tokenize(searchText)
    val regex = tokens.joinToString("|") { s: String -> Pattern.quote(s) }

    return Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
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