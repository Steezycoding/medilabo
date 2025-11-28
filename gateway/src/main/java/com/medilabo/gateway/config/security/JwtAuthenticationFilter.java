package com.medilabo.gateway.config.security;

import com.medilabo.gateway.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	/**
	 * Filter to authenticate requests using JWT tokens.
	 * Extracts the token from the Authorization header, validates it,
	 * and sets the authentication in the security context if valid.
	 *
	 * @param request     HttpServletRequest
	 * @param response    HttpServletResponse
	 * @param filterChain FilterChain
	 *
	 * @throws ServletException in case of any servlet error
	 * @throws IOException      in case of any I/O error
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		log.debug("JwtAuthenticationFilter executing for {}", request.getRequestURI());

		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			log.warn("Missing Authorization header");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT token");
			return;
		}

		String token = authorizationHeader.substring(7);
		if (jwtService.isValid(token)) {
			String username = jwtService.extractUsername(token);
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(username, null, null);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		} else {
			log.warn("Invalid JWT token");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
