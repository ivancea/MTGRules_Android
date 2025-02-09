package com.ivancea.MTGRules.presentation.main.components.list.diff

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.presentation.main.components.list.RulesListItem
import java.util.regex.Pattern


@Composable
@ExperimentalFoundationApi
fun AddedRule(
    rule: Rule,
    isNavigatedRule: Boolean,
    glossaryTermsPatterns: List<Pair<Pattern, String>>,
    searchTextPattern: Pattern?,
    showSymbols: Boolean,
    textInlineContent: Map<String, InlineTextContent>
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .width(16.dp)
                .background(Color.Green)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Added rule",
                tint = Color.Black
            )
        }

        RulesListItem(
            rule = rule,
            isNavigatedRule = isNavigatedRule,
            glossaryTermsPatterns = glossaryTermsPatterns,
            searchTextPattern = searchTextPattern,
            showSymbols = showSymbols,
            textInlineContent = textInlineContent
        )
    }
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PreviewWithSubtitle() {
    AddedRule(
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