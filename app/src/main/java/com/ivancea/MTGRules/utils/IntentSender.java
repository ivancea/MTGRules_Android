package com.ivancea.MTGRules.utils;

import android.content.Context;
import android.content.Intent;

import com.ivancea.MTGRules.MainActivity;
import com.ivancea.MTGRules.constants.Actions;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IntentSender {
	public static void readText(Context context, String text) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(Actions.ACTION_READ);
		intent.putExtra(Actions.DATA, text);

		context.startActivity(intent);
	}

	/**
	 * @param ruleTitle The title of the rule, or "" to go home
	 */
	public static void openRule(Context context, String ruleTitle) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(Actions.ACTION_NAVIGATE_RULE);
		intent.putExtra(Actions.DATA, ruleTitle);

		context.startActivity(intent);
	}

	public static void openRandomRule(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(Actions.ACTION_RANDOM_RULE);

		context.startActivity(intent);
	}

	public static void changeTheme(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(Actions.ACTION_CHANGE_THEME);

		context.startActivity(intent);
	}
}
