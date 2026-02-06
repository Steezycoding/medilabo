package com.medilabo.riskevaluatormicroservice.controller;

import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskEvaluatorController Test Suite")
public class RiskEvaluatorControllerTests {
	private MockMvc mockMvc;

	@InjectMocks
	private RiskEvaluatorController riskEvaluatorController;

	@BeforeEach
	public void setUp() {
		// Initialize MockMvc in 'standaloneSetup' to avoid using Spring context
		mockMvc = MockMvcBuilders.standaloneSetup(riskEvaluatorController).build();
	}

	@Nested
	@DisplayName("ENDPOINT '/risk-evaluator/patient/{id}' Tests")
	class RiskEvaluatorPatientIdTests {
		private final Map<Long, RiskLevel> riskPatientLevels = Map.of(
				1L, RiskLevel.NONE,
				2L, RiskLevel.BORDERLINE,
				3L, RiskLevel.IN_DANGER,
				4L, RiskLevel.EARLY_ONSET
		);

		private static final Long PATIENT_ID = 3L;

		@Test
		@DisplayName("GET /risk-evaluator/patient/{id} : Should respond OK & return the calculated risk level for patient id")
		void getPatientRiskLevelTestWithValidPatient() throws Exception {
			mockMvc.perform(get("/risk-evaluator/patient/{patientId}", PATIENT_ID))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.riskLevel").value(riskPatientLevels.get(PATIENT_ID).name()));
		}
	}
}
