package com.leafchild.scopequotas.data;

/**
 * Created by: leafchild
 * Date: 10/04/2017
 * Project: ScopeQuotas
 */

public enum WorklogType {

	MINUTES("Minutes"),
	HOURS("Hours"),
	DAYS("Days");

	private String value;

	WorklogType(String value) {

		this.value = value;
	}

	public String getValue() {

		return value;
	}

	public static WorklogType fromString(String text) {

		for (WorklogType b : WorklogType.values()) {
			if (b.value.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}

	public static WorklogType fromOrdinal(int n) {

		return values()[n];
	}
}
