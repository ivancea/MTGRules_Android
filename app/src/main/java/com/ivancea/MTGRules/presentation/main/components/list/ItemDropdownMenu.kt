package com.ivancea.MTGRules.presentation.main.components.list

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ShareCompat.IntentBuilder
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.utils.IntentSender

@Composable
fun ItemDropdownMenu(rule: Rule, showMenu: MutableState<Boolean>) {
    val context = LocalContext.current

    var showSearchInRuleDialog by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = showMenu.value,
        onDismissRequest = { showMenu.value = false }
    ) {
        DropdownMenuItem(
            onClick = {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(rule.title, rule.title + ": " + rule.text)
                clipboard.setPrimaryClip(clip)

                showMenu.value = false
            }
        ) { Text(stringResource(R.string.context_copy)) }
        DropdownMenuItem(
            onClick = {
                val text = rule.title + ": " + rule.text

                IntentBuilder(context)
                    .setType("text/plain")
                    .setText(text)
                    .startChooser()

                showMenu.value = false
            }
        ) { Text(stringResource(R.string.context_share)) }
        DropdownMenuItem(
            onClick = {
                IntentSender.readText(context, rule.text)

                showMenu.value = false
            }
        ) { Text(stringResource(R.string.context_read)) }
        DropdownMenuItem(
            onClick = {
                showSearchInRuleDialog = true

                showMenu.value = false
            }
        ) { Text(stringResource(R.string.context_search_in_rule)) }
    }

    if (showSearchInRuleDialog) {
        SearchInRuleDialog(rule) { showSearchInRuleDialog = false }
    }
}