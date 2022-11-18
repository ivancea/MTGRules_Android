package com.ivancea.MTGRules.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ivancea.MTGRules.services.NotificationsService;
import com.ivancea.MTGRules.services.RulesService;
import com.ivancea.MTGRules.services.StorageService;

import java.time.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyPackageReplacedReceiver extends BroadcastReceiver {
	@Inject
	StorageService storageService;

	@Inject
	NotificationsService notificationsService;

	@Inject
	RulesService rulesService;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
			LocalDate currentRulesSource = rulesService.getLatestRulesSource().getDate();
			LocalDate lastSavedRulesSource = storageService.getLastRulesSource();

			if (lastSavedRulesSource == null || currentRulesSource.isAfter(lastSavedRulesSource)) {
				notificationsService.notifyNewRules(currentRulesSource);
			}

			storageService.setLastRulesSource(currentRulesSource);
		}
	}
}
