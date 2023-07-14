package com.ivancea.MTGRules.presentation.main.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.utils.IntentSender
import java.util.regex.Pattern

val IS_PARENT_RULE_PATTERN: Pattern = Pattern.compile("^(\\d{1,3}\\.|Glossary)$")

@Composable
fun RulesListItem(
    rule: Rule
) {
    val context = LocalContext.current
    val withSubtitle = IS_PARENT_RULE_PATTERN.matcher(rule.title).matches()

    Column(Modifier
        .fillMaxWidth()
        .clickable { IntentSender.openRule(context, rule.title, false) }) {
        Row {
            Text(text = rule.title)

            if (withSubtitle) {
                Text(text = rule.text)
            }
        }
        if (!withSubtitle) {
            Text(text = rule.text)
        }
    }
}