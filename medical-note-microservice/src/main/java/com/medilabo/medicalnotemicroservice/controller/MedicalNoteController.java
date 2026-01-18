package com.medilabo.medicalnotemicroservice.controller;

import com.medilabo.medicalnotemicroservice.controller.dto.MedicalNoteDto;
import com.medilabo.medicalnotemicroservice.service.contracts.MedicalNoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/medical-notes")
public class MedicalNoteController {

	private MedicalNoteService medicalNoteService;

	public MedicalNoteController(MedicalNoteService medicalNoteService) {
		this.medicalNoteService = medicalNoteService;
	}

	@GetMapping("/patient/{id}")
	public ResponseEntity<List<MedicalNoteDto>> getPatientMedicalNotes(@PathVariable Integer id) {
		List<MedicalNoteDto> notes = medicalNoteService.getMedicalNotesByPatientId(id);
		log.info("Found {} notes for patient id='{}'", notes.size(), id);

		if (notes.isEmpty()) {
			log.info("No medical notes found for patient ID: {}.", id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(notes);
	}
}
