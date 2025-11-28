package com.medilabo.gateway.controller;

import com.medilabo.gateway.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

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
	public ResponseEntity<Map<String, Object>> token() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (isAuthenticated(auth)) {
			String token = jwtService.generateToken(auth.getName());

			Map<String, Object> body = new HashMap<>();
			body.put("token", token);

			log.debug("JWT generated for {}: {}", auth.getName(), token);
			return ResponseEntity.ok(body);
		}
		log.warn("Unauthorized access attempt to /auth/token");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	private boolean isAuthenticated(Authentication auth) {
		return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
	}
}
