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
}
