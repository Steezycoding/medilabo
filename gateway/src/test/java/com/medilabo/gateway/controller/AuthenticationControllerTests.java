package com.medilabo.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthenticationController Test Suite")
class AuthenticationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Authenticated user should receive 200 OK with JWT cookie")
	void whenAuthenticated_thenReturns200WithJwtToken() throws Exception {
		mockMvc.perform(get("/auth/token"))
				.andExpect(status().isOk())
				.andExpect(cookie().exists("access_token"))
				.andExpect(cookie().httpOnly("access_token", true))
				.andExpect(cookie().maxAge("access_token", 3600))
				.andExpect(content().string(""));
	}

	@Test
	@DisplayName("Unauthenticated user should receive 401 Unauthorized without JWT cookie")
	void whenNotAuthenticated_thenReturns401WithNoJwtToken() throws Exception {
		mockMvc.perform(get("/auth/token"))
				.andExpect(status().isUnauthorized())
				.andExpect(cookie().doesNotExist("access_token"))
				.andExpect(content().string(""));
	}
}