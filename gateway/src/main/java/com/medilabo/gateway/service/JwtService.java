package com.medilabo.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

	@Value("${security.jwt.secret}")
	private String secret;

	@Value("${security.jwt.expiration-seconds}")
	private long expirationSeconds;

	@Value("${security.jwt.issuer}")
	private String issuer;

	private SecretKey secretKey;

	/**
	 * Initialize the JwtService by validating the secret and creating the SecretKey.
	 * This method is annotated @PostConstruct to ensure it runs after the properties are set.
	 */
	@PostConstruct
	public void init() {
		if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException("security.jwt.secret must be set and at least 32 bytes long");
		}
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String username) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

		return Jwts.builder()
				.issuer(issuer)
				.subject(username)
				.issuedAt(now)
				.expiration(expiration)
				.signWith(secretKey)
				.compact();
	}

	public boolean isValid(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
