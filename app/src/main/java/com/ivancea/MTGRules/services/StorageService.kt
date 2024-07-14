package com.ivancea.MTGRules.services

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.ivancea.MTGRules.constants.FirebaseConfig
import com.ivancea.MTGRules.constants.Preferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

class StorageService @Inject constructor(@ApplicationContext context: Context) {
    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(
            Preferences.PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    var useLightTheme: Boolean
        get() = preferences.getBoolean(Preferences.USE_LIGHT_THEME, false)
        set(newUseLightTheme) {
            preferences.edit()
                .putBoolean(Preferences.USE_LIGHT_THEME, newUseLightTheme)
                .apply()
        }

    var showSymbols: Boolean
        get() = preferences.getBoolean(Preferences.SHOW_SYMBOLS, true)
        set(newShowSymbols) {
            preferences.edit()
                .putBoolean(Preferences.SHOW_SYMBOLS, newShowSymbols)
                .apply()
        }

    var showAds: Boolean
        get() = preferences.getBoolean(
            Preferences.SHOW_ADS, FirebaseConfig.getAdsActiveByDefault()
        )
        set(newShowAds) {
            preferences.edit()
                .putBoolean(Preferences.SHOW_ADS, newShowAds)
                .apply()
        }

    var lastRulesSource: LocalDate?
        get() {
            val lastRulesSource = preferences.getString(Preferences.LAST_RULE_SOURCE, null)
                ?: return null
            return try {
                LocalDate.parse(lastRulesSource)
            } catch (e: DateTimeParseException) {
                null
            }
        }
        set(lastRuleSource) {
            preferences.edit()
                .putString(Preferences.LAST_RULE_SOURCE, lastRuleSource.toString())
                .apply()
        }

    var lastOpenedVersion: Long?
        get() {
            val value = preferences.getLong(Preferences.LAST_OPENED_VERSION, 0L)
            return if (value == 0L) null else value
        }
        set(newLastOpenedVersion) {
            if (newLastOpenedVersion == null) {
                preferences.edit()
                    .remove(Preferences.LAST_OPENED_VERSION)
                    .apply()
            } else {
                preferences.edit()
                    .putLong(Preferences.LAST_OPENED_VERSION, newLastOpenedVersion)
                    .apply()
            }
        }

    var notificationsPermissionRequested: Boolean
        get() = preferences.getBoolean(Preferences.NOTIFICATIONS_PERMISSION_REQUESTED, false)
        set(newNotificationsPermissionRequested) {
            preferences.edit()
                .putBoolean(
                    Preferences.NOTIFICATIONS_PERMISSION_REQUESTED,
                    newNotificationsPermissionRequested
                )
                .apply()
        }
}