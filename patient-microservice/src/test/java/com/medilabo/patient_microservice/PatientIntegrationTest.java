package com.medilabo.patient_microservice;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Patient API Integration Test Suite")
public class PatientIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("GET /patients Tests")
	class PatientApiIntegrationTests {
		@Test
		@DisplayName("GET /patients with data should return all patients")
		public void getAllPatients_shouldReturnOkAndJsonFromDb() throws Exception {
			mockMvc.perform(get("/patients"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(4))
					.andExpect(jsonPath("$[0].lastName").value("TestNone"))
					.andExpect(jsonPath("$[0].firstName").value("Test"))
					.andExpect(jsonPath("$[0].birthDate").value("1966-12-31"))
					.andExpect(jsonPath("$[0].gender").value("F"))
					.andExpect(jsonPath("$[0].address").value("1 Brookside St"))
					.andExpect(jsonPath("$[0].phoneNumber").value("100-222-3333"))
					.andExpect(jsonPath("$[1].lastName").value("TestBorderline"))
					.andExpect(jsonPath("$[2].lastName").value("TestInDanger"))
					.andExpect(jsonPath("$[3].lastName").value("TestEarlyOnset"));
		}

		@Test
		@Sql(statements = "DELETE FROM patient")
		@DisplayName("GET /patients with NO data should return No Content")
		void getAllPatients_whenNoData_shouldReturnNoContent() throws Exception {
			mockMvc.perform(get("/patients"))
					.andExpect(status().isNoContent())
					.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));
		}
	}
}
