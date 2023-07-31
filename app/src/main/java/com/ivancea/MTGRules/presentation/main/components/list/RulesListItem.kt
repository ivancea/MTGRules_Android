package com.ivancea.MTGRules.presentation.main.components.list

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.constants.Symbols
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.presentation.common.NonConsumingClickableText
import com.ivancea.MTGRules.utils.IntentSender
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

private val parentRulePattern = Pattern.compile("^(\\d{1,3}\\.|Glossary)$")
private val ruleLinkPattern =
    Pattern.compile("\\b(?<rule>\\d{3})(?:\\.(?<subRule>\\d+)(?<letter>[a-z])?)?\\b")
private val examplePattern = Pattern.compile("^Example:", Pattern.MULTILINE)
private val symbolPattern = Pattern.compile("\\{(?<symbol>[\\w/]+)\\}")

private const val NAVIGATE_RULE_ANNOTATION_KEY = "navigate-rule"

@Composable
@ExperimentalFoundationApi
fun RulesListItem(
    rule: Rule,
    isNavigatedRule: Boolean,
    glossaryTermsPatterns: List<Pair<Pattern, String>>,
    searchTextPattern: Pattern?,
    showSymbols: Boolean,
    textInlineContent: Map<String, InlineTextContent>
) {
    val context = LocalContext.current
    val withSubtitle = parentRulePattern.matcher(rule.title).matches()
    val showMenu = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(isNavigatedRule) {
        if (isNavigatedRule) {
            launch {
                val press = PressInteraction.Press(Offset.Zero)
                interactionSource.emit(press)

                delay(300)

                val release = PressInteraction.Release(press)
                interactionSource.emit(release)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .combinedClickable(
                onClick = { IntentSender.openRule(context, rule.title, false) },
                onLongClick = { showMenu.value = true },
                indication = LocalIndication.current,
                interactionSource = interactionSource
            )
            .padding(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val annotatedRuleTitle = annotateRuleTitle(rule.title, searchTextPattern)

            Text(
                text = annotatedRuleTitle,
                color = MaterialTheme.colors.primary,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            if (withSubtitle) {
                val annotatedRuleSubtitle = annotateRuleTitle(rule.text, searchTextPattern)

                Text(
                    text = annotatedRuleSubtitle,
                    color = MaterialTheme.colors.primary,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
        if (!withSubtitle) {
            val annotatedRuleText =
                annotateRuleText(rule.text, glossaryTermsPatterns, searchTextPattern, showSymbols)

            NonConsumingClickableText(
                text = annotatedRuleText,
                style = TextStyle(color = MaterialTheme.colors.primary),
                inlineContent = textInlineContent,
                onClick = {
                    annotatedRuleText.getStringAnnotations(
                        tag = NAVIGATE_RULE_ANNOTATION_KEY,
                        start = it,
                        end = it
                    ).firstOrNull()?.let { annotation ->
                        IntentSender.openRule(context, annotation.item, false)
                    }
                },
                shouldConsume = {
                    annotatedRuleText.getStringAnnotations(
                        tag = NAVIGATE_RULE_ANNOTATION_KEY,
                        start = it,
                        end = it
                    ).firstOrNull() != null
                }
            )
        }

        ItemDropdownMenu(rule = rule, showMenu = showMenu)
    }
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun annotateRuleTitle(
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
private fun annotateRuleText(
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

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PreviewWithSubtitle() {
    RulesListItem(
        rule = Rule(
            title = "100.",
            text = "My rule 100"
        ),
        isNavigatedRule = false,
        glossaryTermsPatterns = emptyList(),
        searchTextPattern = Pattern.compile("0"),
        showSymbols = true,
        textInlineContent = emptyMap()
    )
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PreviewWithSubtextAndSymbols() {
    val context = LocalContext.current
    val lineHeight = MaterialTheme.typography.body1.lineHeight
    val textInlineContent =
        remember(context, lineHeight) { Symbols.makeSymbolsMap(context, lineHeight) }

    RulesListItem(
        rule = Rule(
            title = "100.5a",
            text = "Symbols: " + Symbols.drawablesBySymbol.keys.joinToString(" ") { "{$it}" }
        ),
        isNavigatedRule = false,
        glossaryTermsPatterns = emptyList(),
        searchTextPattern = Pattern.compile("a"),
        showSymbols = true,
        textInlineContent = textInlineContent
    )
}