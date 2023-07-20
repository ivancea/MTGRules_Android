package com.ivancea.MTGRules.presentation.main.components.list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.utils.IntentSender
import java.util.regex.Pattern

private val IS_PARENT_RULE_PATTERN: Pattern = Pattern.compile("^(\\d{1,3}\\.|Glossary)$")
private val RULE_LINK_PATTERN =
    Pattern.compile("\\b(?<rule>\\d{3})(?:\\.(?<subRule>\\d+)(?<letter>[a-z])?)?\\b")

private const val NAVIGATE_RULE_ANNOTATION_KEY = "navigate-rule"

@Composable
fun RulesListItem(
    rule: Rule,
    rules: List<Rule>
) {
    val context = LocalContext.current
    val withSubtitle = IS_PARENT_RULE_PATTERN.matcher(rule.title).matches()

    Box(modifier = Modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { IntentSender.openRule(context, rule.title, false) }
                .padding(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = rule.title,
                    color = MaterialTheme.colors.primary,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                if (withSubtitle) {
                    Text(
                        text = rule.text,
                        color = MaterialTheme.colors.primary,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }
            if (!withSubtitle) {
                val annotatedRuleText = annotateRuleText(rule, rules)

                ClickableText(
                    text = annotatedRuleText,
                    style = TextStyle(color = MaterialTheme.colors.primary),
                ) {
                    annotatedRuleText.getStringAnnotations(
                        tag = NAVIGATE_RULE_ANNOTATION_KEY,
                        start = it,
                        end = it
                    ).firstOrNull()?.let { annotation ->
                        IntentSender.openRule(context, annotation.item, false)
                    }
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun annotateRuleText(
    rule: Rule,
    rules: List<Rule>
): AnnotatedString {
    val builder = AnnotatedString.Builder(rule.text)

    formatRuleTitleLinks(builder, rule.text)
    formatGlossaryLinks(builder, rule.text, rules)

    return builder.toAnnotatedString()
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun formatRuleTitleLinks(builder: AnnotatedString.Builder, ruleText: String) {
    val linkMatcher = RULE_LINK_PATTERN.matcher(ruleText)

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
    rules: List<Rule>
) {
    val glossaryRule = rules[rules.size - 1]
    val glossaryTerms = glossaryRule.subRules
        .map(Rule::title)
        .sortedByDescending { obj: String -> obj.length }

    for (glossaryTerm in glossaryTerms) {
        // TODO: Cache patterns on rule set change
        val matcher = Pattern.compile(
            "\\b" + makePluralAcceptingGlossaryRegex(Pattern.quote(glossaryTerm)) + "\\b",
            Pattern.CASE_INSENSITIVE
        )
            .matcher(ruleText)
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
            color = MaterialTheme.colors.secondary,
            fontWeight = FontWeight.Bold
        ),
        start,
        end
    )
}

/**
 * Takes a regex, and returns another regex that also accepts plurals.
 *
 * Note: This method should be replaced with a proper pluralization library, or should use translations.
 *
 * @param glossaryRegex The glossary regex to pluralize
 * @return A regex accepting plurals
 */
private fun makePluralAcceptingGlossaryRegex(glossaryRegex: String): String? {
    return "$glossaryRegex(?:s|es)?"
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