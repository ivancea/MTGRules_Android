package com.ivancea.MTGRules.presentation.main.components.list.diff

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesDiff
import com.ivancea.MTGRules.presentation.main.components.list.RulesListItem
import java.util.regex.Pattern

@Composable
@ExperimentalFoundationApi
fun ChangedRule(
    changedRule: RulesDiff.ChangedRule,
    isNavigatedRule: Boolean,
    glossaryTermsPatterns: List<Pair<Pattern, String>>,
    searchTextPattern: Pattern?,
    showSymbols: Boolean,
    sourceTextInlineContent: Map<String, InlineTextContent>,
    targetTextInlineContent: Map<String, InlineTextContent>
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .width(16.dp)
                .background(Color.Gray)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Difference,
                contentDescription = "Changed rule",
                tint = Color.Black
            )
        }

        Column {
            RulesListItem(
                rule = changedRule.sourceRule!!,
                isNavigatedRule = isNavigatedRule,
                glossaryTermsPatterns = glossaryTermsPatterns,
                searchTextPattern = searchTextPattern,
                showSymbols = showSymbols,
                textInlineContent = sourceTextInlineContent
            )

            Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowDown,
                        contentDescription = "Rule changed to..."
                    )
                }
            }

            RulesListItem(
                rule = changedRule.targetRule!!,
                isNavigatedRule = isNavigatedRule,
                glossaryTermsPatterns = glossaryTermsPatterns,
                searchTextPattern = searchTextPattern,
                showSymbols = showSymbols,
                textInlineContent = targetTextInlineContent
            )
        }
    }
}


@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PreviewWithSubtitle() {
    ChangedRule(
        changedRule = RulesDiff.ChangedRule(
            sourceRule = Rule("100.7b", "Changed rule (Old text)"),
            targetRule = Rule("100.7b", "Changed rule (New text)"),
        ),
        isNavigatedRule = false,
        glossaryTermsPatterns = emptyList(),
        searchTextPattern = Pattern.compile("0"),
        showSymbols = true,
        sourceTextInlineContent = emptyMap(),
        targetTextInlineContent = emptyMap()
    )
}