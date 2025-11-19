package com.medilabo.patient_microservice.controller;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.exception.PatientIdNotFoundException;
import com.medilabo.patient_microservice.service.contracts.PatientService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientController Test Suite")
public class PatientControllerTests {
	private MockMvc mockMvc;

	@Mock
	private PatientService patientService;

	@InjectMocks
	private PatientController patientController;

	private List<PatientDto> patientList;

	@BeforeEach
	public void setUp() {
		// Initialize MockMvc in 'standaloneSetup' to avoid using Spring context
		mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

		patientList = List.of(
				createPatientDto("Doe", "John", "1966-12-31", "M", "1 Brookside St", "111-222-3333"),
				createPatientDto("Smith", "Jane", "1974-06-24", "F", "20 Club Road", "444-555-6666")
		);
	}

	@Nested
	@DisplayName("ENDPOINT '/patients' Tests")
	class PatientsTests {
		@Test
		@DisplayName("GET /patients : Should respond OK & return the list of all patients when patients found")
		public void getAllPatientsTest() throws Exception {
			when(patientService.getAll()).thenReturn(patientList);

			mockMvc.perform(get("/patients"))
					.andExpect(status().isOk());

			verify(patientService, times(1)).getAll();
			verifyNoMoreInteractions(patientService);
		}

		@Test
		@DisplayName("GET /patients :  Should respond NO_CONTENT when no patients found")
		public void getAllPatientsEmptyTest() throws Exception {
			when(patientService.getAll()).thenReturn(List.of());

			mockMvc.perform(get("/patients"))
					.andExpect(status().isNoContent());

			verify(patientService, times(1)).getAll();
			verifyNoMoreInteractions(patientService);
		}
	}

	@Nested
	@DisplayName("ENDPOINT '/patients/{id}' Tests")
	class PatientsIdTests {
		@Test
		@DisplayName("GET /patients/{id} : Should respond OK & return the patient when patient found")
		public void getPatientByIdTest() throws Exception {
			Long patientId = 1L;
			PatientDto patient = createPatientDto("Doe", "John", "1966-12-31", "M", "1 Brookside St", "111-222-3333");

			when(patientService.getById(patientId)).thenReturn(patient);

			mockMvc.perform(get("/patients/{id}", patientId))
					.andExpect(status().isOk());

			verify(patientService, times(1)).getById(patientId);
			verifyNoMoreInteractions(patientService);
		}

		@Test
		@DisplayName("GET /patients/{id} : Should respond NOT_FOUND when patient NOT found")
		public void getPatientByIdNotFoundTest() throws Exception {
			Long nonExistentPatientId = -1L;
			doThrow(new PatientIdNotFoundException(nonExistentPatientId)).when(patientService).getById(anyLong());

			mockMvc.perform(get("/patients/{id}", nonExistentPatientId))
					.andExpect(status().isNotFound());

			verify(patientService, times(1)).getById(nonExistentPatientId);
			verifyNoMoreInteractions(patientService);
		}
	}

	private PatientDto createPatientDto(String lastName, String firstName, String birthDate, String gender, String address, String phoneNumber) {
		return PatientDto.builder()
				.lastName(lastName)
				.firstName(firstName)
				.birthDate(birthDate)
				.gender(gender)
				.address(address)
				.phoneNumber(phoneNumber)
				.build();
	}
}
