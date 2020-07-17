package com.ivancea.MTGRules.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class HistoryItem {
	public enum Type {
		Ignored,
		Rule,
		Search,
		Random
	}

	private final Type type;
	private final Serializable value;
	// private final double offset;
}
