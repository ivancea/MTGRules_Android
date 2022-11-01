package com.ivancea.MTGRules.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.ivancea.MTGRules.R;
import com.ivancea.MTGRules.constants.Notifications;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.inject.Inject;

public class NotificationsService {

	private final Context context;

	private final NotificationManager notificationManager;

	@Inject
	public NotificationsService(Context context) {
		this.context = context;
		this.notificationManager = context.getSystemService(NotificationManager.class);

		configureChannel();
	}

	public void notifyNewRules(LocalDate lastRuleSource) {
		String title = context.getString(R.string.notification_channel_new_rules_name);
		String body = context.getString(
			R.string.notification_channel_new_rules_body,
			lastRuleSource.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
		);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Notifications.NEW_RULES_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_notification)
			.setColor(Color.BLACK)
			.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
			.setContentTitle(title)
			.setContentText(body)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		int notificationId = Notifications.NEW_RULES_NOTIFICATION_BASE_ID +
			lastRuleSource.getYear() * 1_00_00 +
			lastRuleSource.getMonthValue() * 1_00 +
			lastRuleSource.getDayOfMonth();

		notificationManager.notify(notificationId, builder.build());
	}

	private void configureChannel() {
		String name = context.getString(R.string.notification_channel_new_rules_name);
		String description = context.getString(R.string.notification_channel_new_rules_description);

		NotificationChannel channel = new NotificationChannel(
			Notifications.NEW_RULES_CHANNEL_ID,
			name,
			NotificationManager.IMPORTANCE_DEFAULT
		);
		channel.setDescription(description);

		notificationManager.createNotificationChannel(channel);
	}
}