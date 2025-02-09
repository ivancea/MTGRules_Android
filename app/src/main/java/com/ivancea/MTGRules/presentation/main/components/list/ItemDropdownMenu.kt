package com.ivancea.MTGRules.presentation.main.components.list

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
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
            text = { Text(stringResource(R.string.context_copy)) },
            onClick = {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(rule.title, rule.title + ": " + rule.text)
                clipboard.setPrimaryClip(clip)

                showMenu.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.context_share)) },
            onClick = {
                val text = rule.title + ": " + rule.text

                IntentBuilder(context)
                    .setType("text/plain")
                    .setText(text)
                    .startChooser()

                showMenu.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.context_read)) },
            onClick = {
                IntentSender.readText(context, rule.text)

                showMenu.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.context_search_in_rule)) },
            onClick = {
                showSearchInRuleDialog = true

                showMenu.value = false
            }
        )
    }

    if (showSearchInRuleDialog) {
        SearchInRuleDialog(rule) { showSearchInRuleDialog = false }
    }
}