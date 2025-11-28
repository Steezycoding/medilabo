package com.medilabo.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthenticationController Test Suite")
class AuthenticationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "alice")
	@DisplayName("Authenticated user should receive 200 OK with JWT token")
	void whenAuthenticated_thenReturns200WithJwtToken() throws Exception {
		mockMvc.perform(get("/auth/token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isString());
	}

	@Test
	@DisplayName("Unauthenticated user should receive 401 Unauthorized")
	void whenNotAuthenticated_thenReturns401WithNoJwtToken() throws Exception {
		mockMvc.perform(get("/auth/token"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.token").doesNotExist());
	}
}