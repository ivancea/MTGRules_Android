package com.ivancea.MTGRules.services;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.ivancea.MTGRules.constants.Preferences;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class StorageService {

	private final SharedPreferences preferences;

	@Inject
	public StorageService(@ApplicationContext Context context) {
		this.preferences = context.getSharedPreferences(
			Preferences.PREFERENCES_NAME,
			Context.MODE_PRIVATE
		);
	}

	public boolean getUseLightTheme() {
		return preferences.getBoolean(Preferences.USE_LIGHT_THEME, false);
	}

	public void setUseLightTheme(boolean useLightTheme) {
		preferences.edit()
			.putBoolean(Preferences.USE_LIGHT_THEME, useLightTheme)
			.apply();
	}

	public boolean getShowSymbols() {
		return preferences.getBoolean(Preferences.SHOW_SYMBOLS, true);
	}

	public void setShowSymbols(boolean showSymbols) {
		preferences.edit()
			.putBoolean(Preferences.SHOW_SYMBOLS, showSymbols)
			.apply();
	}

	@Nullable
	public LocalDate getLastRulesSource() {
		String lastRulesSource = preferences.getString(Preferences.LAST_RULE_SOURCE, null);

		if (lastRulesSource == null) {
			return null;
		}

		try {
			return LocalDate.parse(lastRulesSource);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	public void setLastRulesSource(LocalDate lastRuleSource) {
		preferences.edit()
			.putString(Preferences.LAST_RULE_SOURCE, lastRuleSource.toString())
			.apply();
	}
}
