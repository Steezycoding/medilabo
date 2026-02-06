package com.medilabo.riskevaluatormicroservice.service;

import com.medilabo.riskevaluatormicroservice.beans.PatientBean;
import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;
import com.medilabo.riskevaluatormicroservice.exception.PatientNotFoundException;
import com.medilabo.riskevaluatormicroservice.proxies.PatientMicroserviceProxy;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskEvaluatorService Test Suite")
public class RiskEvaluatorServiceTests {

	@Mock
	private PatientMicroserviceProxy patientProxy;

	@InjectMocks
	private RiskEvaluatorServiceImpl riskEvaluatorService;

	private Map<Long, RiskLevel> riskPatientLevels;

	@BeforeEach
	public void setUp() {
		riskPatientLevels = Map.of(
				1L, RiskLevel.NONE,
				2L, RiskLevel.BORDERLINE,
				3L, RiskLevel.IN_DANGER,
				4L, RiskLevel.EARLY_ONSET
		);
	}

	@Nested
	@DisplayName("evaluate() Tests")
	class EvaluateTests {
		private static final Long VALID_PATIENT_ID = 2L;
		private static final Long INVALID_PATIENT_ID = 999L;
		private static final PatientBean VALID_PATIENT = PatientBean.builder()
				.id(VALID_PATIENT_ID)
				.firstName("John")
				.lastName("Doe")
				.birthDate("1974-06-24")
				.gender("M")
				.address("Test Address")
				.phoneNumber("123-456-7890")
				.build();

		@Test
		@DisplayName("Should return risk level for valid patient id")
		public void givenValidPatientId_whenEvaluate_thenReturnRiskLevel() {
			when(patientProxy.getPatientById(anyLong())).thenReturn(VALID_PATIENT);

			RiskLevel result = riskEvaluatorService.evaluate(VALID_PATIENT_ID);

			assertThat(result).isEqualTo(riskPatientLevels.get(VALID_PATIENT_ID));
		}

		@Test
		@DisplayName("Should throw PatientNotFoundException if patient ID NOT found")
		public void givenInvalidPatientId_whenEvaluate_thenReturnRiskLevel() {
			when(patientProxy.getPatientById(anyLong())).thenThrow(feign404(INVALID_PATIENT_ID));

			PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () ->
					riskEvaluatorService.evaluate(INVALID_PATIENT_ID)
			);

			String expectedExceptionMessage = String.format("Patient not found with id: %d", INVALID_PATIENT_ID);
			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
		}
	}

	private FeignException.NotFound feign404(Long patientId) {
		return new FeignException.NotFound(
				String.format("Patient with ID '%d' doesn't exist.", patientId),
				Request.create(
						Request.HttpMethod.GET,
						String.format("/patients/%d", patientId),
						Map.of(),
						null,
						StandardCharsets.UTF_8,
						null
				),
				null,
				Map.of()
		);
	}
}
