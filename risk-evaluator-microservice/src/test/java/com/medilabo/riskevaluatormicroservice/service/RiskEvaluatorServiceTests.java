package com.medilabo.riskevaluatormicroservice.service;

import com.medilabo.riskevaluatormicroservice.beans.MedicalNoteBean;
import com.medilabo.riskevaluatormicroservice.beans.PatientBean;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	class GetRiskLevelTests {
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

		@Nested
		@DisplayName("evaluate() Tests")
		class EvaluateTests {

			@Test
			@DisplayName("Should return NONE when medical notes are null")
			public void givenNullMedicalNotes_whenEvaluate_thenReturnNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "M");

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(null);

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}

			@Test
			@DisplayName("Should return NONE when medical notes are empty")
			public void givenEmptyMedicalNotes_whenEvaluate_thenReturnNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "F");

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of());

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}

			@Test
			@DisplayName("Should return NONE when no trigger terms are found")
			public void givenMedicalNotesWithoutTriggers_whenEvaluate_thenReturnNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "M");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("Patient is healthy, no relevant terms here")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}

			/**
			 * <b>WARNING</b>: This test covers a patient with age exactly 30 years old.
			 * It will return <em>RiskLevel.NONE</em> by default, as the requirements do not
			 * specify the risk evaluation criteria for this age group.
			 */
			@Test
			@DisplayName("Should return NONE when patient age is exactly 30")
			public void givenPatientAgeExactly30_whenEvaluate_thenReturnNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 30, "M");

				// Important : Ensure that the medical note contains at least one trigger term to verify that the age condition is the one leading to the NONE risk level
				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}

			@Test
			@DisplayName("Should return NONE for under 30 male with 1 trigger")
			public void givenUnder30MaleWithOneTrigger_whenEvaluate_thenReturnNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 25, "M");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}

			@Test
			@DisplayName("Should return NONE for under 30 female with 5 distinct triggers")
			public void givenUnder30FemaleWithFiveTriggers_whenEvaluate_thenReturnNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 25, "F");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine poids taille cholesterol")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}

			@Test
			@DisplayName("Should return NONE for over 30 with 1 trigger")
			public void givenOver30WithOneTrigger_whenEvaluate_thenReturnNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "M");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("fumeur")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}
		}

		@Nested
		@DisplayName("evaluateUnder30() tests")
		class EvaluateUnder30Tests {

			@Test
			@DisplayName("Under30 male with >=5 triggers -> EARLY_ONSET")
			public void under30MaleFiveOrMoreTriggers_thenEarlyOnset() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 25, "M");

				// 5 distinct trigger terms
				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine poids taille cholesterol")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.EARLY_ONSET);
			}

			@Test
			@DisplayName("Under30 male with exactly 3 triggers -> IN_DANGER")
			public void under30MaleThreeTriggers_thenInDanger() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 25, "M");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine cholesterol")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.IN_DANGER);
			}

			@Test
			@DisplayName("Under30 female with exactly 4 triggers -> IN_DANGER")
			public void under30FemaleFourTriggers_thenInDanger() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 25, "F");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine poids taille")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.IN_DANGER);
			}

			@Test
			@DisplayName("Under30 female with >=7 triggers -> EARLY_ONSET")
			public void under30FemaleSevenOrMoreTriggers_thenEarlyOnset() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 25, "F");

				// 7 distinct trigger terms (use available trigger words)
				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine poids taille cholesterol fumeur vertiges")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.EARLY_ONSET);
			}
		}

		@Nested
		@DisplayName("evaluateOver30() tests")
		class EvaluateOver30Tests {

			@Test
			@DisplayName("Over30 with 2 triggers -> BORDERLINE")
			public void over30TwoTriggers_thenBorderline() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "M");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.BORDERLINE);
			}

			@Test
			@DisplayName("Over30 with 6 triggers -> IN_DANGER")
			public void over30SixTriggers_thenInDanger() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "F");

				// 6 distinct trigger terms
				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine poids taille cholesterol fumeur")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.IN_DANGER);
			}

			@Test
			@DisplayName("Over30 with 7 triggers -> IN_DANGER")
			public void over30SevenTriggers_thenInDanger() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "F");

				// 6 distinct trigger terms
				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine poids taille cholesterol fumeur anormal")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.IN_DANGER);
			}

			@Test
			@DisplayName("Over30 with 8 triggers -> EARLY_ONSET")
			public void over30EightTriggers_thenEarlyOnset() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "M");

				// 8 distinct trigger terms (use available trigger words)
				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine microalbumine poids taille cholesterol fumeur vertiges rechute")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.EARLY_ONSET);
			}

			@Test
			@DisplayName("Over30 with 1 trigger -> NONE")
			public void over30OneTrigger_thenNone() {
				Long patientId = 1L;
				PatientBean patient = generatePatientBean(patientId, 40, "F");

				MedicalNoteBean note = MedicalNoteBean.builder()
						.patId(patientId.intValue())
						.note("hemoglobine")
						.build();

				when(patientProxy.getPatientById(anyLong())).thenReturn(patient);
				when(medicalNoteProxy.getPatientMedicalNotes(anyLong())).thenReturn(List.of(note));

				RiskLevel result = riskEvaluatorService.getRiskLevel(patientId);
				assertThat(result).isEqualTo(RiskLevel.NONE);
			}
		}
	}

	/**
	 * Helper method to create a FeignException.NotFound instance for a given patient ID.
	 *
	 * @param patientId The ID of the patient that was not found, used to construct the exception message and request details.
	 *
	 * @return A FeignException.NotFound instance with a message indicating that the patient with the specified ID doesn't exist,
	 * and a request object representing the failed request to retrieve the patient information.
	 */
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

	/**
	 * Helper method to generate a PatientBean instance with the specified ID, age, and gender
	 *
	 * @param patId     The ID to assign to the generated PatientBean instance
	 * @param patAge    The age to assign to the generated PatientBean instance
	 * @param patGender The gender to assign to the generated PatientBean instance ("M" for male, "F" for female)
	 *
	 * @return A PatientBean instance with the specified ID, birthdate calculated and gender. Other fields left as default (null or empty)
	 */
	private PatientBean generatePatientBean(Long patId, int patAge, String patGender) {
		return PatientBean.builder()
				.id(patId)
				.birthDate(LocalDate.now().minusYears(patAge).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.gender(patGender)
				.build();
	}
}
