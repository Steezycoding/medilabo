package com.medilabo.riskevaluatormicroservice.service;

import com.medilabo.riskevaluatormicroservice.beans.MedicalNoteBean;
import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;
import com.medilabo.riskevaluatormicroservice.exception.PatientNotFoundException;
import com.medilabo.riskevaluatormicroservice.proxies.MedicalNoteMicroserviceProxy;
import com.medilabo.riskevaluatormicroservice.proxies.PatientMicroserviceProxy;
import com.medilabo.riskevaluatormicroservice.utils.RiskEvaluatorDataTest;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
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

	@Mock
	private MedicalNoteMicroserviceProxy medicalNoteProxy;

	@InjectMocks
	private RiskEvaluatorServiceImpl riskEvaluatorService;

	@Nested
	@DisplayName("getRiskLevel() Tests")
	class EvaluateTests {
		private static final Long INVALID_PATIENT_ID = 999L;

		/*
		 * This test iterates through all valid patients defined in the RiskEvaluatorDataTest class,
		 * retrieves the corresponding medical notes for each patient, and verifies that the getRiskLevel method
		 * returns the expected risk level for each patient.
		 */
		@Test
		@DisplayName("Should return valid risk level for each valid patient id")
		public void givenValidPatientIds_whenEvaluate_thenReturnRiskLevel() {
			RiskEvaluatorDataTest.patientsList.forEach((riskLevel, validPatient) -> {
				System.out.printf("Testing patient ID '%d' with expected risk level '%s'%n", validPatient.getId(), riskLevel);
				List<MedicalNoteBean> validPatientNotes = RiskEvaluatorDataTest.medicalNotes.stream()
						.filter(note -> note.getPatId().longValue() == validPatient.getId())
						.toList();
				when(patientProxy.getPatientById(anyLong())).thenReturn(validPatient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(validPatientNotes);

				RiskLevel result = riskEvaluatorService.getRiskLevel(validPatient.getId());

				assertThat(result).isEqualTo(riskLevel);
			});
		}

		@Test
		@DisplayName("Should throw PatientNotFoundException if patient ID NOT found")
		public void givenInvalidPatientId_whenEvaluate_thenReturnRiskLevel() {
			when(patientProxy.getPatientById(anyLong())).thenThrow(feign404(INVALID_PATIENT_ID));

			PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () ->
					riskEvaluatorService.getRiskLevel(INVALID_PATIENT_ID)
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
