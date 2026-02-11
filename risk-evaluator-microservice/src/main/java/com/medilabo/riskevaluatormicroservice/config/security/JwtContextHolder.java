package com.medilabo.riskevaluatormicroservice.config.security;

/**
 * Thread-safe holder for JWT tokens in the current request context
 */
public class JwtContextHolder {

	private static final ThreadLocal<String> JWT_CONTEXT = new ThreadLocal<>();

	public static void setToken(String token) {
		JWT_CONTEXT.set(token);
	}

	public static String getToken() {
		return JWT_CONTEXT.get();
	}

	public static void clear() {
		JWT_CONTEXT.remove();
	}
}

