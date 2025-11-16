package com.medilabo.gateway.controller;

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

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	/**
	 * Endpoint to check if the user is authenticated.
	 * Returns the principal name if authenticated, otherwise returns 401 Unauthorized.
	 *
	 * @return ResponseEntity with authentication status and principal name if authenticated
	 */
	@GetMapping("/check")
	public ResponseEntity<Map<String, Object>> check() {
		Map<String, Object> body = new HashMap<>();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
			body.put("principal", auth.getName());
			return ResponseEntity.ok(body);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}
}
