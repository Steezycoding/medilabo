package com.medilabo.gateway.constant;

public enum CookieTokenType {
	ACCESS("access_token"),
	REFRESH("refresh_token");

	private final String value;

	CookieTokenType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
