package com.medilabo.medicalnotemicroservice.controller.dto;

import com.medilabo.medicalnotemicroservice.domain.MedicalNote;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicalNoteDto {
	private String id;
	private Integer patId;
	private String patient;
	private String note;

	public static MedicalNoteDto fromEntity(MedicalNote note) {
		return MedicalNoteDto.builder()
				.id(note.getId())
				.patId(note.getPatId())
				.patient(note.getPatient())
				.note(note.getNote())
				.build();
	}
}
