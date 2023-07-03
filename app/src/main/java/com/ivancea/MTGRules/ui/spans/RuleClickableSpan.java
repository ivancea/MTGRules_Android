package com.ivancea.MTGRules.ui.spans;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.ivancea.MTGRules.utils.IntentSender;

public class RuleClickableSpan extends ClickableSpan {
	private final String ruleTitle;

	public RuleClickableSpan(String ruleTitle) {
		this.ruleTitle = ruleTitle;
	}

	public String getRuleTitle() {
		return ruleTitle;
	}

	@Override
	public void onClick(@NonNull View widget) {
		IntentSender.openRule(widget.getContext(), ruleTitle, false);
	}

	@Override
	public void updateDrawState(@NonNull TextPaint ds) {
		ds.setColor(ds.linkColor);
		ds.setUnderlineText(false);
	}
}
