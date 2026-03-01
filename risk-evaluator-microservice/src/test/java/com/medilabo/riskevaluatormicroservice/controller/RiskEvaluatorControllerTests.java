package com.medilabo.riskevaluatormicroservice.controller;

import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;
import com.medilabo.riskevaluatormicroservice.service.contracts.RiskEvaluatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskEvaluatorController Test Suite")
public class RiskEvaluatorControllerTests {
	private MockMvc mockMvc;

	@Mock
	private RiskEvaluatorService riskEvaluatorService;

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
		private static final Long PATIENT_ID = 3L;
		private static final RiskLevel IN_DANGER = RiskLevel.IN_DANGER;

		@Test
		@DisplayName("GET /risk-evaluator/patient/{id} : Should respond OK & return the calculated risk level for patient id")
		void getPatientRiskLevelTestWithValidPatient() throws Exception {
			when(riskEvaluatorService.getRiskLevel(anyLong())).thenReturn(IN_DANGER);

			mockMvc.perform(get("/risk-evaluator/patient/{patientId}", PATIENT_ID))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.riskLevel").value(IN_DANGER.name()));

			verify(riskEvaluatorService, times(1)).getRiskLevel(eq(PATIENT_ID));
			verifyNoMoreInteractions(riskEvaluatorService);
		}
	}
}
