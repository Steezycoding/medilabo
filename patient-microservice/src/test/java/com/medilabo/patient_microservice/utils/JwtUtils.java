package com.medilabo.patient_microservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {
	@Value("${security.jwt.gateway-shared-issuer}")
	private String issuer;

	@Value("${security.jwt.gateway-shared-secret}")
	private String secret;

	private SecretKey secretKey;

	/**
	 * Initialize the JwtUtils by creating the SecretKey.
	 * This method is annotated @PostConstruct to ensure it runs after the properties are set.
	 */
	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generates a JWT bearer token for testing purposes.
	 *
	 * @return A JWT bearer token string.
	 */
	public String generateToken() {
		Instant now = Instant.now();

		String jwt = Jwts.builder()
				.subject("integration-test-user")
				.issuer(issuer)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(3600)))
				.signWith(secretKey)
				.compact();

		return "Bearer " + jwt;
	}
}
