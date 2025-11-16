package com.medilabo.gateway.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security.cors")
public class CorsProperties {

	private Allowed allowed = new Allowed();
	private boolean credentials;

	public static class Allowed {
		private List<String> origins;
		private List<String> methods;
		private List<String> headers;

		public List<String> getOrigins() {
			return origins;
		}

		public void setOrigins(List<String> origins) {
			this.origins = origins;
		}

		public List<String> getMethods() {
			return methods;
		}

		public void setMethods(List<String> methods) {
			this.methods = methods;
		}

		public List<String> getHeaders() {
			return headers;
		}

		public void setHeaders(List<String> headers) {
			this.headers = headers;
		}
	}

	public Allowed getAllowed() {
		return allowed;
	}

	public void setAllowed(Allowed allowed) {
		this.allowed = allowed;
	}

	public boolean isAllowedCredentials() {
		return credentials;
	}

	public void setCredentials(boolean credentials) {
		this.credentials = credentials;
	}
}