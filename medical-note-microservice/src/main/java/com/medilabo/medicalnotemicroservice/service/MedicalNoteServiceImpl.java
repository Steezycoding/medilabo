package com.medilabo.medicalnotemicroservice.service;

import com.medilabo.medicalnotemicroservice.controller.dto.MedicalNoteDto;
import com.medilabo.medicalnotemicroservice.repository.MedicalNoteRepository;
import com.medilabo.medicalnotemicroservice.service.contracts.MedicalNoteService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
