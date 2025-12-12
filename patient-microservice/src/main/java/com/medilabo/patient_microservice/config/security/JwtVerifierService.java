package com.medilabo.patient_microservice.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtVerifierService {
	@Value("${security.jwt.gateway-shared-secret}")
	private String secret;

	@Value("${security.jwt.gateway-shared-issuer}")
	private String issuer;

	private SecretKey secretKey;

	/**
	 * Initialize the JwtVerifierService by validating the secret and creating the SecretKey.
	 * This method is annotated @PostConstruct to ensure it runs after the properties are set.
	 */
	@PostConstruct
	public void init() {
		if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException("security.jwt.gateway-shared-secret must be set and at least 32 bytes long");
		}
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public Jws<Claims> verify(String token) {
		// throws si signature invalide, token expiré, issuer incorrect, etc.
		return Jwts.parser()
				.requireIssuer(issuer)
				.verifyWith(secretKey)
				.build()
				.parseClaimsJws(token);
	}
}
