package com.ivancea.MTGRules.services

import android.app.AlertDialog
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import com.google.firebase.analytics.FirebaseAnalytics
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.constants.Events
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ChangelogService @Inject constructor(
    @param:ActivityContext private val context: Context,
    private val storageService: StorageService
) {
    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    data class ChangelogEntry(
        /**
         * The version code over which this changes was applied.
         * That is, if the change was made while the app was in version 2, this should be 2,
         * even if the change is deployed in version 3.
         */
        val version: Long,
        @StringRes val changes: List<Int>
    )

    private val changelogEntries = listOf(
        ChangelogEntry(
            version = 75,
            changes = listOf(
                R.string.changelog_added_external_links_menu
            )
        )
    )

    fun notifyChangelog() {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val currentVersion = getLongVersionCode(packageInfo)
        val savedVersion = storageService.lastOpenedVersion

        storageService.lastOpenedVersion = currentVersion

        // TODO: Uncomment after a month to avoid notifying new users
        /*if (savedVersion == null) {
            return
        }*/

        val changelogEntriesToNotify = changelogEntries.filter { it.version >= (savedVersion ?: 0) }

        if (changelogEntriesToNotify.isEmpty()) {
            return
        }

        firebaseAnalytics.logEvent(Events.CHANGELOG_ALERT_SHOWN, null)

        AlertDialog.Builder(context)
            .setTitle(R.string.alert_changelog_title)
            .setMessage(changelogEntriesToNotify.joinToString("\n") { entry ->
                 entry.changes.joinToString("\n") { "• " + context.getString(it) }
            })
            .show()

    }
}