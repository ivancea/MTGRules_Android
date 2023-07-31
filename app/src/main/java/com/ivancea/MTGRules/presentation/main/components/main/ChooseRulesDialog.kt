package com.ivancea.MTGRules.presentation.main.components.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.presentation.MainViewModel
import com.ivancea.MTGRules.presentation.common.RulesPickerDialog

@Composable
fun ChooseRulesDialog(
    onClose: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    RulesPickerDialog(
        title = stringResource(R.string.dialog_select_rules),
        onSuccess = { chosenRulesSource ->
            viewModel.useRules(chosenRulesSource)
            viewModel.logEvent(Events.CHANGE_RULES)
            onClose()
        },
        onCancel = { onClose() }
    )
}