package com.ivancea.MTGRules.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.presentation.MainViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun RulesPickerDialog(
    title: String,
    onSuccess: (chosenRulesSource: RulesSource) -> Unit,
    onCancel: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val rulesSources = viewModel.rulesService.rulesSources;

    BaseDialog(
        onDismissed = { onCancel() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                ),
            )
            Divider()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false)
            ) {
                rulesSources.asReversed().forEach { rulesSource ->
                    ClickableText(
                        AnnotatedString(
                            rulesSource.date.format(
                                DateTimeFormatter.ofLocalizedDate(
                                    FormatStyle.MEDIUM
                                )
                            )
                        ),
                        onClick = { onSuccess(rulesSource) },
                        style = MaterialTheme.typography.titleLarge.merge(
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Divider()
            Row(horizontalArrangement = Arrangement.End) {
                Button(onClick = { onCancel() }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        }
    }
}