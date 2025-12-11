package com.medilabo.gateway.controller;

import com.medilabo.gateway.constant.CookieTokenType;
import com.medilabo.gateway.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthenticationController Test Suite")
public class AuthenticationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtService jwtService;

	@Value("${security.jwt.access-expiration-seconds}")
	private int accessExpirationSeconds;

	@Value("${security.jwt.refresh-expiration-seconds}")
	private int refreshExpirationSeconds;

	@Nested
	@DisplayName("/auth/token Endpoint Tests")
	class TokenEndpointTests {

		@Test
		@WithMockUser(username = "alice")
		@DisplayName("GET /auth/token Authenticated user should receive 200 OK with JWT cookies")
		void whenAuthenticated_thenReturns200WithJwtToken() throws Exception {
			mockMvc.perform(get("/auth/token"))
					.andExpect(status().isOk())
					.andExpect(cookie().exists("access_token"))
					.andExpect(cookie().exists("refresh_token"))
					.andExpect(cookie().httpOnly("access_token", true))
					.andExpect(cookie().httpOnly("refresh_token", true))
					.andExpect(cookie().maxAge("access_token", accessExpirationSeconds))
					.andExpect(cookie().maxAge("refresh_token", refreshExpirationSeconds))
					.andExpect(content().string(""));
		}

		@Test
		@DisplayName("GET /auth/token Unauthenticated user should receive 401 Unauthorized without JWT cookie")
		void whenNotAuthenticated_thenReturns401WithNoJwtToken() throws Exception {
			mockMvc.perform(get("/auth/token"))
					.andExpect(status().isUnauthorized())
					.andExpect(cookie().doesNotExist("access_token"))
					.andExpect(content().string(""));
		}
	}

	@Nested
	@DisplayName("/auth/check Endpoint Tests")
	class CheckEndpointTests {

		@Test
		@DisplayName("GET /auth/check with valid token should return 200 OK")
		void whenValidToken_thenReturns200() throws Exception {
			String validToken = "valid-access-token";
			when(jwtService.resolveTokenFromCookie(any(HttpServletRequest.class), any(CookieTokenType.class))).thenReturn(validToken);
			when(jwtService.isValid(anyString())).thenReturn(true);
			when(jwtService.extractUsername(anyString())).thenReturn("alice");

			mockMvc.perform(get("/auth/check").cookie(new Cookie("access_token", validToken)))
					.andExpect(status().isOk());
		}

		@Test
		@DisplayName("GET /auth/check with invalid token should return 401 Unauthorized")
		void whenInvalidToken_thenReturns401() throws Exception {
			String invalidToken = "invalid-access-token";
			when(jwtService.resolveTokenFromCookie(any(HttpServletRequest.class), any(CookieTokenType.class))).thenReturn(invalidToken);
			when(jwtService.isValid(anyString())).thenReturn(false);

			mockMvc.perform(get("/auth/check")
							.cookie(new Cookie("access_token", invalidToken))
					)
					.andExpect(status().isUnauthorized());
		}
	}

	@Nested
	@DisplayName("/auth/refresh Endpoint Tests")
	class RefreshEndpointTests {
		@Test
		@DisplayName("POST /auth/refresh with valid token should return 200 OK with new JWT cookies")
		void whenValidRefreshToken_thenReturns200WithNewJwtToken() throws Exception {
			String newAccessToken = "new-access-token";
			String newRefreshToken = "new-refresh-token";

			when(jwtService.resolveTokenFromCookie(any(HttpServletRequest.class), any(CookieTokenType.class))).thenReturn("valid-refresh-token");
			when(jwtService.isValid(anyString())).thenReturn(true);
			when(jwtService.extractUsername(anyString())).thenReturn("alice");
			when(jwtService.generateToken(anyString(), anyLong())).thenReturn(newAccessToken, newRefreshToken);

			mockMvc.perform(post("/auth/refresh")
							.cookie(new Cookie("refresh_token", "valid-refresh-token"))
					)
					.andExpect(status().isOk())
					.andExpect(cookie().exists("access_token"))
					.andExpect(cookie().exists("refresh_token"))
					.andExpect(cookie().value("access_token", newAccessToken))
					.andExpect(cookie().value("refresh_token", newRefreshToken))
					.andExpect(cookie().httpOnly("access_token", true))
					.andExpect(cookie().httpOnly("refresh_token", true))
					.andExpect(cookie().maxAge("access_token", accessExpirationSeconds))
					.andExpect(cookie().maxAge("refresh_token", refreshExpirationSeconds))
					.andExpect(content().string(""));
		}

		@Test
		@DisplayName("POST /auth/refresh with invalid token should return 401 Unauthorized")
		void whenInvalidRefreshToken_thenReturns401() throws Exception {
			String invalidToken = "invalid-access-token";
			when(jwtService.resolveTokenFromCookie(any(HttpServletRequest.class), any(CookieTokenType.class))).thenReturn(invalidToken);
			when(jwtService.isValid(anyString())).thenReturn(false);

			mockMvc.perform(post("/auth/refresh")
							.cookie(new Cookie("refresh_token", invalidToken))
					)
					.andExpect(status().isUnauthorized());
		}
	}

	@Nested
	@DisplayName("/auth/logout Endpoint Tests")
	class LogoutEndpointTests {
		@Test
		@DisplayName("POST /auth/logout Any client should logout successfully")
		void whenAnyClient_thenLogoutSuccessfully() throws Exception {
			mockMvc.perform(post("/auth/logout"))
					.andExpect(status().isOk())
					.andExpect(cookie().exists("access_token"))
					.andExpect(cookie().exists("refresh_token"))
					.andExpect(cookie().value("access_token", ""))
					.andExpect(cookie().value("refresh_token", ""))
					.andExpect(cookie().maxAge("access_token", 0))
					.andExpect(cookie().maxAge("refresh_token", 0))
					.andExpect(content().string(""));
		}
	}
}