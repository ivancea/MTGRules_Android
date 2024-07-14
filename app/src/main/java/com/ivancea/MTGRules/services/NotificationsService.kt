package com.ivancea.MTGRules.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.ivancea.MTGRules.MainActivity
import com.ivancea.MTGRules.R
import com.ivancea.MTGRules.constants.Events
import com.ivancea.MTGRules.constants.Notifications
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

class NotificationsService @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    init {
        configureChannel()
    }

    fun notifyNewRules(lastRuleSource: LocalDate) {
        val title = context.getString(R.string.notification_channel_new_rules_name)
        val body = context.getString(
            R.string.notification_channel_new_rules_body,
            lastRuleSource.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        )
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(
            context, Notifications.NEW_RULES_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.BLACK)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationId =
            Notifications.NEW_RULES_NOTIFICATION_BASE_ID + lastRuleSource.year * 10000 + lastRuleSource.monthValue * 100 +
                    lastRuleSource.dayOfMonth

        notificationManager.notify(notificationId, builder.build())
        firebaseAnalytics.logEvent(Events.NEW_RULES_NOTIFICATION_SENT, null)
    }

    private fun configureChannel() {
        val name = context.getString(R.string.notification_channel_new_rules_name)
        val description = context.getString(R.string.notification_channel_new_rules_description)
        val channel = NotificationChannel(
            Notifications.NEW_RULES_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = description
        notificationManager.createNotificationChannel(channel)
    }
}