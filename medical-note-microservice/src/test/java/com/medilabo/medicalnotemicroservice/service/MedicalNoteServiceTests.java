package com.medilabo.medicalnotemicroservice.service;

import com.medilabo.medicalnotemicroservice.controller.dto.MedicalNoteDto;
import com.medilabo.medicalnotemicroservice.domain.MedicalNote;
import com.medilabo.medicalnotemicroservice.repository.MedicalNoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalNote Test Suite")
class MedicalNoteServiceTests {
	@Mock
	private MedicalNoteRepository medicalNoteRepository;

	@InjectMocks
	private MedicalNoteServiceImpl medicalNoteService;

	@Nested
	@DisplayName("getMedicalNotesByPatientId() Tests")
	class GetMedicalNotesByPatientIdTests {

		@Test
		@DisplayName("Should return medical notes for given patient ID")
		public void givenPatientId_whenGetMedicalNotesByPatientId_thenReturnMedicalNotes() {
			List<MedicalNote> medicalNotes = List.of(
					new MedicalNote("a123b456", 1, "JohnDoe", "Prescribed new medication."),
					new MedicalNote("c789d012", 1, "JohnDoe", "Patient shows signs of improvement.")
			);

			when(medicalNoteRepository.getMedicalNotesByPatId(anyInt())).thenReturn(medicalNotes);

			List<MedicalNoteDto> result = medicalNoteService.getMedicalNotesByPatientId(1);

			assertThat(result).hasSize(2);
			assertThat(result.get(0).getId()).isEqualTo("a123b456");
			assertThat(result.get(1).getId()).isEqualTo("c789d012");

			verify(medicalNoteRepository).getMedicalNotesByPatId(eq(1));
			verifyNoMoreInteractions(medicalNoteRepository);
		}
	}
}