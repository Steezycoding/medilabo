package com.medilabo.patient_microservice.service;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.domain.Patient;
import com.medilabo.patient_microservice.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Month;
import java.util.List;

import static com.medilabo.patient_microservice.utils.DateUtils.createDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Test Suite")
public class PatientServiceTests {

	@Mock
	private PatientRepository patientRepository;

	@InjectMocks
	private PatientServiceImpl patientService;

	@Nested
	@DisplayName("getAll() Tests")
	class GetAllTests {
		@Test
		@DisplayName("Should return patients")
		public void getAllPatientsTest() {
			List<Patient> patients = List.of(
					new Patient(1L, "Doe", "John", createDate(1966, Month.DECEMBER, 31), "M", "1 Brookside St", "111-222-3333"),
					new Patient(2L, "Smith", "Jane", createDate(1974, Month.JUNE, 24), "F", "20 Club Road", "444-555-6666")
			);

			when(patientRepository.findAll()).thenReturn(patients);

			List<PatientDto> result = patientService.getAll();

			assertThat(result).hasSize(2);

			verify(patientRepository).findAll();
			verifyNoMoreInteractions(patientRepository);
		}
	}
}
