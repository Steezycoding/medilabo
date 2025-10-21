package com.medilabo.patient_microservice.controller;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.service.contracts.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/patients")
public class PatientController {
	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@GetMapping
	public ResponseEntity<List<PatientDto>> getAllPatients() {
		log.info("GET /patients: Retrieving all patients");

		List<PatientDto> patients = patientService.getAll();

		if (patients.isEmpty()) {
			log.warn("GET /patients: No patients found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.contentType(MediaType.APPLICATION_JSON)
					.build();
		}

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(patients);
	}
}
