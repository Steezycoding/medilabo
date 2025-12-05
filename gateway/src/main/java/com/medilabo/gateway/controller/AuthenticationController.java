package com.medilabo.gateway.controller;

import com.medilabo.gateway.service.JwtService;
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

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Value("${security.jwt.expiration-seconds}")
	private long jwtExpirationSeconds;

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
			String token = jwtService.generateToken(auth.getName());

			log.debug("JWT generated for {}: {}", auth.getName(), token);

			ResponseCookie jwtCookie = ResponseCookie.from("access_token", token)
					.httpOnly(true)
					.secure(false)
					.sameSite("Strict")
					.path("/")
					.maxAge(jwtExpirationSeconds)
					.build();

			return ResponseEntity
					.ok()
					.header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
					.build();
		}

		log.warn("Unauthorized access attempt to /auth/token");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		ResponseCookie deleteCookie = ResponseCookie.from("access_token", "")
				.httpOnly(true)
				.secure(false)
				.sameSite("Strict")
				.path("/")
				.maxAge(0)
				.build();

		log.debug("User logged out — clearing JWT cookie");

		return ResponseEntity
				.ok()
				.header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
				.build();
	}

	private boolean isAuthenticated(Authentication auth) {
		return auth != null
				&& auth.isAuthenticated()
				&& !(auth instanceof AnonymousAuthenticationToken);
	}
}
