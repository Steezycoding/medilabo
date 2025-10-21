package com.medilabo.patient_microservice.service;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.service.contracts.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Test Suite")
public class PatientServiceTests {

	private PatientService patientService;

	public PatientServiceTests() {
		this.patientService = new PatientServiceImpl();
	}

	@Nested
	@DisplayName("getAll() Tests")
	class GetAllTests {
		@Test
		@DisplayName("Should return no patients")
		public void getAllTest() {
			List<PatientDto> result = patientService.getAll();

			assertThat(result).isEmpty();
		}
	}
}
