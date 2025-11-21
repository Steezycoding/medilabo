package com.medilabo.patient_microservice.controller;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.exception.PatientIdNotFoundException;
import com.medilabo.patient_microservice.service.contracts.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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

	@GetMapping("/{id}")
	public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
		log.info("GET /patients/{}: Retrieving patient by ID", id);

		PatientDto patient = patientService.getById(id);

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(patient);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PatientDto> updatePatient(@PathVariable Long id, @RequestBody PatientDto patientDto) {
		log.info("PUT /patients/{}: Updating patient...", id);

		PatientDto updatedPatient = patientService.update(id, patientDto);

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(updatedPatient);
	}

	@PostMapping()
	public ResponseEntity<PatientDto> createPatient(@RequestBody PatientDto patientDto) throws ParseException {
		log.info("POST /patients: Creating patient...");

		PatientDto createdPatient = patientService.create(patientDto);

		return ResponseEntity.status(HttpStatus.CREATED)
				.contentType(MediaType.APPLICATION_JSON)
				.body(createdPatient);
	}

	@ExceptionHandler(PatientIdNotFoundException.class)
	public ResponseEntity<String> handlePatientIdNotFoundException(PatientIdNotFoundException ex) {
		log.warn("PatientIdNotFoundException: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.contentType(MediaType.APPLICATION_JSON)
				.body(ex.getMessage());
	}
}
