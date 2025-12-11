package com.medilabo.gateway.controller;

import com.medilabo.gateway.constant.CookieTokenType;
import com.medilabo.gateway.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Value("${security.jwt.access-expiration-seconds}")
	private long accessExpirationSeconds;

	@Value("${security.jwt.refresh-expiration-seconds}")
	private long refreshExpirationSeconds;

	private final JwtService jwtService;

	public AuthenticationController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	/**
	 * Endpoint to generate a JWT for authenticated users.
	 * Uses the authenticated principal name as the subject.
	 *
	 * @return ResponseEntity containing the generated JWT
	 */
	@GetMapping("/token")
	public ResponseEntity<Void> token() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (isAuthenticated(auth)) {
			String accessToken = jwtService.generateToken(auth.getName(), accessExpirationSeconds);
			String refreshToken = jwtService.generateToken(auth.getName(), refreshExpirationSeconds);

			log.debug("JWT generated for {}: {}", auth.getName(), accessToken);

			ResponseCookie jwtCookie = ResponseCookie.from("access_token", accessToken)
					.httpOnly(true)
					.secure(false)
					.sameSite("Strict")
					.path("/")
					.maxAge(accessExpirationSeconds)
					.build();

			ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
					.httpOnly(true)
					.secure(false)
					.sameSite("Strict")
					.path("/")
					.maxAge(refreshExpirationSeconds)
					.build();

			return ResponseEntity
					.ok()
					.header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
					.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
					.build();
		}

		log.warn("Unauthorized access attempt to /auth/token");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	/**
	 * Endpoint to check the validity of the JWT from cookies.
	 *
	 * @param request the HttpServletRequest containing the cookies
	 *
	 * @return ResponseEntity indicating whether the token is valid
	 */
	@GetMapping("/check")
	public ResponseEntity<Void> check(HttpServletRequest request) {
		String accessToken = jwtService.resolveTokenFromCookie(request, CookieTokenType.ACCESS);
		if (accessToken == null || !jwtService.isValid(accessToken)) {
			log.info("/auth/check UNAUTHORIZED: invalid or missing token.");
			log.debug("Access Token: {}", accessToken == null ? "Null" : "Invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String username = jwtService.extractUsername(accessToken);
		log.info("/auth/check OK. Subject: {}", username);
		return ResponseEntity.ok().build();
	}

	/**
	 * Endpoint to refresh the JWT using a valid refresh token from cookies.
	 *
	 * @param request the HttpServletRequest containing the cookies
	 *
	 * @return ResponseEntity containing the new JWT
	 */
	@PostMapping("/refresh")
	public ResponseEntity<Void> refresh(HttpServletRequest request) {
		String refreshToken = jwtService.resolveTokenFromCookie(request, CookieTokenType.REFRESH);
		if (refreshToken == null || !jwtService.isValid(refreshToken)) {
			log.info("/auth/refresh UNAUTHORIZED: invalid or missing token.");
			log.debug("Refresh Token: {}", refreshToken == null ? "Null" : "Invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String username = jwtService.extractUsername(refreshToken);
		String newAccessToken = jwtService.generateToken(username, accessExpirationSeconds);
		String newRefreshToken = jwtService.generateToken(username, refreshExpirationSeconds);

		ResponseCookie newAccessCookie = ResponseCookie.from("access_token", newAccessToken)
				.httpOnly(true)
				.secure(false)
				.sameSite("Strict")
				.path("/")
				.maxAge(accessExpirationSeconds)
				.build();

		ResponseCookie newRefreshCookie = ResponseCookie.from("refresh_token", newRefreshToken)
				.httpOnly(true)
				.secure(false)
				.sameSite("Strict")
				.path("/")
				.maxAge(refreshExpirationSeconds)
				.build();

		log.info("Access token refreshed for {}", username);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
				.header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
				.build();
	}

	/**
	 * Endpoint to log out the user by clearing JWT cookies.
	 *
	 * @return ResponseEntity indicating successful logout
	 */
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		ResponseCookie deleteAccessToken = ResponseCookie.from("access_token", "")
				.httpOnly(true)
				.secure(false)
				.sameSite("Strict")
				.path("/")
				.maxAge(0)
				.build();

		ResponseCookie deleteRefreshToken = ResponseCookie.from("refresh_token", "")
				.httpOnly(true)
				.secure(false)
				.sameSite("Strict")
				.path("/")
				.maxAge(0)
				.build();

		log.debug("User logged out — clearing JWT cookies");

		return ResponseEntity
				.ok()
				.header(HttpHeaders.SET_COOKIE, deleteAccessToken.toString())
				.header(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString())
				.build();
	}

	private boolean isAuthenticated(Authentication auth) {
		return auth != null
				&& auth.isAuthenticated()
				&& !(auth instanceof AnonymousAuthenticationToken);
	}
}
