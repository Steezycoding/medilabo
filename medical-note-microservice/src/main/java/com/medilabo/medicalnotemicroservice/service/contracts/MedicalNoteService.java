package com.medilabo.medicalnotemicroservice.service.contracts;

import com.medilabo.medicalnotemicroservice.controller.dto.MedicalNoteDto;

import java.util.List;

public interface MedicalNoteService {
	List<MedicalNoteDto> getMedicalNotesByPatientId(Integer patientId);

	MedicalNoteDto create(MedicalNoteDto medicalNoteDto);
}
