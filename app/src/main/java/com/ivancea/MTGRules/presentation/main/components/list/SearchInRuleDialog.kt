package com.ivancea.MTGRules.presentation.main.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.presentation.common.BaseDialog
import com.ivancea.MTGRules.utils.IntentSender

@Composable
fun SearchInRuleDialog(rule: Rule, onClose: () -> Unit) {
    val context = LocalContext.current
    var value by remember { mutableStateOf("") }

    val trimmedValue = value.trim()

    BaseDialog(
        onDismissed = { onClose() }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.context_search_in_rule_dialog_title,
                    rule.title
                ),
                style = MaterialTheme.typography.h6
            )
            Text(
                stringResource(R.string.context_search_in_rule_dialog_message),
                style = MaterialTheme.typography.subtitle1
            )

            TextField(value = value, onValueChange = { value = it })

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onClose() }
                ) {
                    Text(stringResource(R.string.dialog_cancel))
                }

                Button(
                    enabled = trimmedValue.isNotEmpty(),
                    onClick = {
                        if (trimmedValue.isNotEmpty()) {
                            IntentSender.openSearch(
                                context,
                                trimmedValue,
                                rule.title,
                                false
                            )
                        }

                        onClose()
                    }
                ) {
                    Text(stringResource(R.string.dialog_ok))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SearchInRuleDialog(rule = Rule("100.1", "Some text"), onClose = {})
}