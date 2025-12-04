package com.medilabo.gateway.config.security;

import com.medilabo.gateway.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

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

		String token = resolveToken(request);

		if (token == null) {
			log.warn("Missing JWT token within request cookies");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT token within request cookies");
			return;
		}

		try {
			if (!jwtService.isValid(token)) {
				log.warn("Invalid JWT token");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
				return;
			}

			String username = jwtService.extractUsername(token);
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(username, null, null);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
			mutableRequest.putHeader("Authorization", "Bearer " + token);
		} catch (Exception e) {
			log.warn("Error while validating JWT token", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
			return;
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Extracts the JWT token from the request cookies.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return the JWT token if present, null otherwise
	 */
	private String resolveToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("access_token".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	/**
	 * Adds the ability to modify headers in the HttpServletRequest.
	 */
	private static class MutableHttpServletRequest extends HttpServletRequestWrapper {
		private final Map<String, String> customHeaders = new HashMap<>();

		public MutableHttpServletRequest(HttpServletRequest request) {
			super(request);
		}

		public void putHeader(String name, String value) {
			customHeaders.put(name, value);
		}

		@Override
		public String getHeader(String name) {
			String headerValue = customHeaders.get(name);
			if (headerValue != null) {
				return headerValue;
			}
			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			String headerValue = customHeaders.get(name);
			if (headerValue != null) {
				List<String> values = new ArrayList<>();
				values.add(headerValue);
				Enumeration<String> original = super.getHeaders(name);
				while (original.hasMoreElements()) {
					values.add(original.nextElement());
				}
				return Collections.enumeration(values);
			}
			return super.getHeaders(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			List<String> names = Collections.list(super.getHeaderNames());
			for (String name : customHeaders.keySet()) {
				if (!names.contains(name)) {
					names.add(name);
				}
			}
			return Collections.enumeration(names);
		}
	}
}
