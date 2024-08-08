package com.ivancea.MTGRules.presentation.main.components.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.presentation.common.RulesPickerDialog
import com.ivancea.MTGRules.utils.IntentSender

@Composable
fun ChooseRulesDialog(
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    RulesPickerDialog(
        title = stringResource(R.string.dialog_select_rules),
        onSuccess = { chosenRulesSource ->
            IntentSender.loadRulesSource(context, chosenRulesSource)
            onClose()
        },
        onCancel = { onClose() }
    )
}