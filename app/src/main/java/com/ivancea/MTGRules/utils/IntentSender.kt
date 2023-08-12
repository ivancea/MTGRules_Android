package com.ivancea.MTGRules.utils

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import com.ivancea.MTGRules.MainActivity
import com.ivancea.MTGRules.constants.Actions

object IntentSender {
    @JvmStatic
    fun readText(context: Context, text: String?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Actions.ACTION_READ
        intent.putExtra(Actions.DATA, text)
        context.startActivity(intent)
    }

    /**
     * @param ruleTitle The title of the rule, or "" to go home
     */
    @JvmStatic
    fun openRule(context: Context, ruleTitle: String?, ignoreHistory: Boolean = false) {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Actions.ACTION_NAVIGATE_RULE
        intent.putExtra(Actions.DATA, ruleTitle)
        if (ignoreHistory) {
            intent.putExtra(Actions.IGNORE_HISTORY, true);
        }
        context.startActivity(intent)
    }

    @JvmStatic
    fun openRandomRule(context: Context, seed: Int? = null, ignoreHistory: Boolean = false) {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Actions.ACTION_RANDOM_RULE
        intent.putExtra(Actions.DATA, seed)
        if (ignoreHistory) {
            intent.putExtra(Actions.IGNORE_HISTORY, true);
        }
        context.startActivity(intent)
    }

    /**
     * @param rootRule The title of the root rule to search from. If provided, only that rule and subrules will be searched
     */
    @JvmStatic
    fun openSearch(context: Context, searchText: String, rootRule: String? = null, ignoreHistory: Boolean = false) {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Intent.ACTION_SEARCH
        intent.putExtra(SearchManager.QUERY, searchText)
        intent.putExtra(Actions.ROOT_RULE, rootRule)
        if (ignoreHistory) {
            intent.putExtra(Actions.IGNORE_HISTORY, true);
        }
        context.startActivity(intent)
    }

    @JvmStatic
    fun changeTheme(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Actions.ACTION_CHANGE_THEME
        context.startActivity(intent)
    }

    @JvmStatic
    fun toggleSymbols(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Actions.ACTION_TOGGLE_SYMBOLS
        context.startActivity(intent)
    }

    @JvmStatic
    fun toggleAds(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Actions.ACTION_TOGGLE_ADS
        context.startActivity(intent)
    }
}