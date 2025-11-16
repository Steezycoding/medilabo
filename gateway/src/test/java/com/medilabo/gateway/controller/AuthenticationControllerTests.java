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
	@DisplayName("Authenticated user should receive 200 OK with principal name")
	void whenAuthenticated_thenReturns200WithPrincipal() throws Exception {
		mockMvc.perform(get("/auth/check"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.principal").value("alice"));
	}

	@Test
	@DisplayName("Unauthenticated user should receive 401 Unauthorized")
	void whenNotAuthenticated_thenReturns401WithEmptyPrincipal() throws Exception {
		mockMvc.perform(get("/auth/check"))
				.andExpect(status().isUnauthorized());
	}
}