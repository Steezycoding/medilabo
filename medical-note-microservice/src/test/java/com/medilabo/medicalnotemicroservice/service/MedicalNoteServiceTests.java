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

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalNoteService Test Suite")
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
					new MedicalNote("a123b456", 1, "JohnDoe", "Prescribed new medication.", new Date()),
					new MedicalNote("c789d012", 1, "JohnDoe", "Patient shows signs of improvement.", new Date())
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

	@Nested
	@DisplayName("create() Tests")
	class CreateTests {
		@Test
		@DisplayName("Should create and return new medical note")
		public void givenMedicalNoteDto_whenCreate_thenReturnCreatedMedicalNoteDto() {
			Date now = new Date();
			MedicalNoteDto newMedicalNote = MedicalNoteDto.builder()
					.patId(1)
					.patient("JohnDoe")
					.note("Initial consultation.")
					.build();
			MedicalNote createdMedicalNote = new MedicalNote("e345f678", 1, "JohnDoe", "Initial consultation.", now);

			when(medicalNoteRepository.save(any(MedicalNote.class))).thenReturn(createdMedicalNote);

			MedicalNoteDto result = medicalNoteService.create(newMedicalNote);

			assertThat(result.getId()).isEqualTo("e345f678");
			assertThat(result.getPatId()).isEqualTo(1);
			assertThat(result.getPatient()).isEqualTo("JohnDoe");
			assertThat(result.getNote()).isEqualTo("Initial consultation.");
			assertThat(result.getCreatedAt()).isEqualTo(now);

			verify(medicalNoteRepository).save(eq(newMedicalNote.toEntity()));
			verifyNoMoreInteractions(medicalNoteRepository);
		}
	}

	@Nested
	@DisplayName("delete() Tests")
	class DeleteTests {
		@Test
		@DisplayName("Should delete note with given id and return the id")
		public void givenNoteId_whenDelete_thenReturnDeletedNoteId() {
			String noteIdToDelete = "e345f678";

			String result = medicalNoteService.delete(noteIdToDelete);

			assertThat(result).isEqualTo("e345f678");

			verify(medicalNoteRepository).deleteMedicalNoteById(eq(noteIdToDelete));
			verifyNoMoreInteractions(medicalNoteRepository);
		}
	}
}