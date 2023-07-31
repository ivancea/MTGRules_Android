package com.ivancea.MTGRules.presentation.main.components.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.presentation.MainViewModel
import com.ivancea.MTGRules.presentation.common.RulesPickerDialog

@Composable
fun CompareRulesDialog(
    onClose: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    var sourceRules by remember { mutableStateOf<RulesSource?>(null) }

    if (sourceRules == null) {
        RulesPickerDialog(
            title = stringResource(R.string.dialog_select_source_rules),
            onSuccess = { chosenSourceRules ->
                sourceRules = chosenSourceRules
            },
            onCancel = { onClose() }
        )
    } else {
        RulesPickerDialog(
            title = stringResource(R.string.dialog_select_target_rules),
            onSuccess = { chosenTargetRules ->
                viewModel.compareRules(
                    sourceRules!!,
                    chosenTargetRules
                )
                viewModel.logEvent(Events.COMPARE_RULES)
                onClose()
            },
            onCancel = { onClose() }
        )
    }
}