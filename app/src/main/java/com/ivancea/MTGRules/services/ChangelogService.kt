package com.ivancea.MTGRules.services

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import com.google.firebase.analytics.FirebaseAnalytics
import com.ivancea.MTGRules.MainActivity
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.constants.Notifications
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

class ChangelogService @Inject constructor(
    @param:ActivityContext private val context: Context,
    private val storageService: StorageService
) {
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

        AlertDialog.Builder(context)
            .setTitle(R.string.alert_changelog_title)
            .setMessage(changelogEntriesToNotify.joinToString("\n") { entry ->
                 entry.changes.joinToString("\n") { "• " + context.getString(it) }
            })
            .show()

    }
}