package com.medilabo.medicalnotemicroservice.controller;

import com.medilabo.medicalnotemicroservice.controller.dto.MedicalNoteDto;
import com.medilabo.medicalnotemicroservice.service.contracts.MedicalNoteService;
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

import static com.medilabo.medicalnotemicroservice.utils.JsonUtils.asJsonString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalNoteController Test Suite")
class MedicalNoteControllerTests {
	private MockMvc mockMvc;

	@Mock
	private MedicalNoteService medicalNoteService;

	@InjectMocks
	private MedicalNoteController medicalNoteController;

	private List<MedicalNoteDto> medicalNoteList;

	@BeforeEach
	void setUp() {
		// Initialize MockMvc in 'standaloneSetup' to avoid using Spring context
		mockMvc = MockMvcBuilders.standaloneSetup(medicalNoteController).build();

		medicalNoteList = List.of(
				createMedicalNoteDto("a123b456", 1, "JohnDoe", "Prescribed new medication."),
				createMedicalNoteDto("c789d012", 1, "JohnDoe", "Patient shows signs of improvement.")
		);
	}

	@Nested
	@DisplayName("ENDPOINT '/medical-notes/patient/{id}' Tests")
	class MedicalNotesPatientIdTests {
		@Test
		@DisplayName("GET /medical-notes/patient/{id} : Should respond OK & return the list of all notes with patient id")
		void getPatientMedicalNotesTest() throws Exception {
			when(medicalNoteService.getMedicalNotesByPatientId(anyInt())).thenReturn(medicalNoteList);

			mockMvc.perform(get("/medical-notes/patient/{id}", 1))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.length()").value(2))
					.andExpect(jsonPath("$[0].id").value("a123b456"))
					.andExpect(jsonPath("$[0].patId").value(1))
					.andExpect(jsonPath("$[0].patient").value("JohnDoe"))
					.andExpect(jsonPath("$[0].note").value("Prescribed new medication."))
					.andExpect(jsonPath("$[1].id").value("c789d012"))
					.andExpect(jsonPath("$[1].patId").value(1))
					.andExpect(jsonPath("$[1].patient").value("JohnDoe"))
					.andExpect(jsonPath("$[1].note").value("Patient shows signs of improvement."));

			verify(medicalNoteService, times(1)).getMedicalNotesByPatientId(eq(1));
			verifyNoMoreInteractions(medicalNoteService);
		}

		@Test
		@DisplayName("GET /medical-notes/patient/{id} : Should respond NO_CONTENT when no notes set with patient id")
		void getPatientMedicalNotesWithEmptyNotesTest() throws Exception {
			when(medicalNoteService.getMedicalNotesByPatientId(anyInt())).thenReturn(List.of());

			mockMvc.perform(get("/medical-notes/patient/{id}", 1))
					.andExpect(status().isNoContent());

			verify(medicalNoteService, times(1)).getMedicalNotesByPatientId(eq(1));
			verifyNoMoreInteractions(medicalNoteService);
		}
	}

	@Nested
	@DisplayName("ENDPOINT '/medical-notes' Tests")
	class MedicalNotesTests {
		@Test
		@DisplayName("POST /medical-notes : Should respond CREATED & create a new medical note")
		void getAllMedicalNotesTest() throws Exception {
			MedicalNoteDto newMedicalNote = createMedicalNoteDto(null, 1, "JohnDoe", "Annual check-up completed.");
			MedicalNoteDto createdMedicalNote = createMedicalNoteDto("e345f678", 1, "JohnDoe", "Annual check-up completed.");
			when(medicalNoteService.create(any(MedicalNoteDto.class))).thenReturn(createdMedicalNote);

			mockMvc.perform(post("/medical-notes")
							.contentType("application/json")
							.content(asJsonString(newMedicalNote)))
					.andExpect(status().isCreated());

			verify(medicalNoteService, times(1)).create(eq(newMedicalNote));
			verifyNoMoreInteractions(medicalNoteService);
		}

	}

	private MedicalNoteDto createMedicalNoteDto(String id, Integer patId, String patient, String note) {
		return MedicalNoteDto.builder()
				.id(id)
				.patId(patId)
				.patient(patient)
				.note(note)
				.build();
	}
}