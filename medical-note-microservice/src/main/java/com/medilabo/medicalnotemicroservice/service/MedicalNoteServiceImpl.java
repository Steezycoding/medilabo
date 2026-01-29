package com.medilabo.medicalnotemicroservice.service;

import com.medilabo.medicalnotemicroservice.controller.dto.MedicalNoteDto;
import com.medilabo.medicalnotemicroservice.domain.MedicalNote;
import com.medilabo.medicalnotemicroservice.repository.MedicalNoteRepository;
import com.medilabo.medicalnotemicroservice.service.contracts.MedicalNoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MedicalNoteServiceImpl implements MedicalNoteService {

	private final MedicalNoteRepository medicalNoteRepository;

	public MedicalNoteServiceImpl(MedicalNoteRepository medicalNoteRepository) {
		this.medicalNoteRepository = medicalNoteRepository;
	}

	/**
	 * Retrieves medical notes for a specific patient by his ID.
	 *
	 * @param patientId The ID of the patient whose medical notes are to be retrieved.
	 *
	 * @return A list of MedicalNoteDto objects representing the patient's medical notes.
	 */
	@Override
	public List<MedicalNoteDto> getMedicalNotesByPatientId(Integer patientId) {
		return medicalNoteRepository.getMedicalNotesByPatId(patientId).stream()
				.map(MedicalNoteDto::fromEntity)
				.toList();
	}

	/**
	 * Creates a new medical note.
	 *
	 * @param medicalNoteDto The MedicalNoteDto object containing the details of the medical note to be created.
	 *
	 * @return The created MedicalNoteDto object.
	 */
	@Override
	public MedicalNoteDto create(MedicalNoteDto medicalNoteDto) {
		medicalNoteDto.setCreatedAt(new Date());
		MedicalNote createdMedicalNoteEntity = medicalNoteRepository.save(medicalNoteDto.toEntity());

		MedicalNoteDto createdMedicalNoteDto = MedicalNoteDto.fromEntity(createdMedicalNoteEntity);
		log.info("MedicalNote created with ID '{}'.", createdMedicalNoteDto.getId());

		return createdMedicalNoteDto;
	}

	/**
	 * Deletes a medical note by its ID.
	 *
	 * @param id The ID of the medical note to be deleted.
	 *
	 * @return The ID of the deleted medical note.
	 */
	@Override
	public String delete(String id) {
		medicalNoteRepository.deleteMedicalNoteById(id);
		log.info("MedicalNote with ID '{}' has been deleted.", id);

		return id;
	}
}
