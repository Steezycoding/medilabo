package com.medilabo.medicalnotemicroservice.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class GatewayJwtFilter extends OncePerRequestFilter {
	private final JwtVerifierService jwtVerifierService;

	public GatewayJwtFilter(JwtVerifierService jwtVerifierService) {
		this.jwtVerifierService = jwtVerifierService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Bearer token");
		} else {
			String token = authHeader.substring(7);

			try {
				Jws<Claims> jws = jwtVerifierService.verify(token);
				String username = jws.getPayload().getSubject();

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						username,
						null,
						Collections.emptyList()
				);
				SecurityContextHolder.getContext().setAuthentication(auth);

				filterChain.doFilter(request, response);
			} catch (JwtException e) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
			}
		}
	}
}