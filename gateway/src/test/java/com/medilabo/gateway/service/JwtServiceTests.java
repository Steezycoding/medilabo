package com.medilabo.gateway.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtServiceTests {
	private static final String VALID_SECRET = "01234567890123456789012345678901"; // 32+ bytes
	private static final String TOO_SHORT_SECRET = "0123456789";
	private static final long DEFAULT_EXPIRATION_SECONDS = 3600L;
	private static final String ISSUER = "medilabo-gateway";

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService();
	}

	/**
	 * Utility method to set private fields via reflection.
	 *
	 * @param target    The object whose field is to be set
	 * @param fieldName The name of the field to set
	 * @param value     The value to set the field to
	 */
	private static void setField(Object target, String fieldName, Object value) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException("Unable to set field " + fieldName, e);
		}
	}

	/**
	 * Utility method to get private fields via reflection.
	 *
	 * @param target    The object whose field is to be retrieved
	 * @param fieldName The name of the field to retrieve
	 *
	 * @return The value of the field
	 */
	private static Object getField(Object target, String fieldName) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(target);
		} catch (Exception e) {
			throw new RuntimeException("Unable to get field " + fieldName, e);
		}
	}

	@Nested
	class InvalidSecretTests {
		@BeforeEach
		void setUp() {
			jwtService = new JwtService();

			setField(jwtService, "secret", TOO_SHORT_SECRET);
			setField(jwtService, "expirationSeconds", DEFAULT_EXPIRATION_SECONDS);
			setField(jwtService, "issuer", ISSUER);
		}

		@Test
		@DisplayName("Should throw exception when secret is too short")
		void shouldThrowExceptionWhenSecretIsTooShort() {
			IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
				jwtService.init();
			});

			assertThat(exception.getMessage()).isEqualTo("security.jwt.secret must be set and at least 32 bytes long");
		}
	}

	@Nested
	class ValidSecretTests {
		@BeforeEach
		void setUp() {
			jwtService = new JwtService();

			setField(jwtService, "secret", VALID_SECRET);
			setField(jwtService, "expirationSeconds", DEFAULT_EXPIRATION_SECONDS);
			setField(jwtService, "issuer", ISSUER);

			jwtService.init();
		}

		@Test
		@DisplayName("Should create valid token with correct subject and issuer")
		void shouldCreateValidTokenWithCorrectSubjectAndIssuer() {
			String username = "john.doe";

			String token = jwtService.generateToken(username);

			assertThat(token).isNotNull();
			assertThat(token).isNotBlank();
		}

		@Test
		@DisplayName("Should validate a valid token")
		void shouldValidateValidToken() {
			String username = "john.doe";
			String token = jwtService.generateToken(username);

			boolean isValid = jwtService.isValid(token);

			assertThat(isValid).isTrue();
		}

		@Test
		@DisplayName("Should extract subject from token")
		void shouldExtractSubjectFromToken() {
			String username = "john.doe";
			String token = jwtService.generateToken(username);

			String extracted = jwtService.extractUsername(token);

			assertThat(extracted).isEqualTo(username);
		}

		@Test
		@DisplayName("Should return invalid when token is signed with different secret")
		void shouldReturnFalseWhenSignatureDoesNotMatch() {
			String username = "john.doe";

			// Forge a token with a different secret key
			String otherSecret = "another-secret-key-which-is-long-enough-123";
			SecretKey otherKey = Keys.hmacShaKeyFor(otherSecret.getBytes(StandardCharsets.UTF_8));
			String forgedToken = Jwts.builder()
					.subject(username)
					.issuedAt(new Date())
					.expiration(new Date(System.currentTimeMillis() + DEFAULT_EXPIRATION_SECONDS * 1000))
					.issuer(ISSUER)
					.signWith(otherKey)
					.compact();

			boolean isValid = jwtService.isValid(forgedToken);

			assertThat(isValid).isFalse();
		}
	}

}
