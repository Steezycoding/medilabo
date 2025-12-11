package com.medilabo.gateway.service;

import com.medilabo.gateway.constant.CookieTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

	@Value("${security.jwt.secret}")
	private String secret;

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

	/**
	 * Generate a JWT token for the given username.
	 *
	 * @param username the username to include in the token subject
	 *
	 * @return the generated JWT token as a String
	 */
	public String generateToken(String username, long expirationSeconds) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

		log.debug("Generating token for user: {}, issued at: {}, expires at: {}", username, now, expiration);

		return Jwts.builder()
				.issuer(issuer)
				.subject(username)
				.issuedAt(now)
				.expiration(expiration)
				.signWith(secretKey)
				.compact();
	}

	/**
	 * Resolve the JWT token from the cookies in the HttpServletRequest with the specified type.
	 *
	 * @param request the HttpServletRequest containing the cookies
	 * @param type    the type of token to resolve (ACCESS or REFRESH)
	 *
	 * @return the JWT token as a String, or null if not found
	 */
	public String resolveTokenFromCookie(HttpServletRequest request, CookieTokenType type) {
		Cookie[] cookies = request.getCookies();
		String cookieType = type.getValue();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieType.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	/**
	 * Validate the given JWT token.
	 *
	 * @param token the JWT token to validate
	 *
	 * @return true if the token is valid and not expired, false otherwise
	 */
	public boolean isValid(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Extract the username from the given JWT token.
	 *
	 * @param token the JWT token
	 *
	 * @return the username (subject) extracted from the token
	 */
	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	/**
	 * Parse the claims from the given JWT token.
	 *
	 * @param token the JWT token
	 *
	 * @return the Claims object containing the token's claims
	 */
	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
