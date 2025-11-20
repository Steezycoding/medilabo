package com.medilabo.patient_microservice.service;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.exception.PatientIdNotFoundException;
import com.medilabo.patient_microservice.repository.PatientRepository;
import com.medilabo.patient_microservice.service.contracts.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;

	public PatientServiceImpl(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@Override
	public List<PatientDto> getAll() {
		return patientRepository.findAll()
				.stream()
				.map(PatientDto::fromEntityWithId)
				.toList();
	}

	@Override
	public PatientDto getById(Long patientId) {
		return patientRepository.findById(patientId)
				.map(PatientDto::fromEntity)
				.orElseThrow(() -> new PatientIdNotFoundException(patientId));
	}

	@Override
	public PatientDto update(Long id, PatientDto patient) {
		return patientRepository.findById(id)
				.map(existingPatient -> {
					// Convert String birthDate to Date
					LocalDate updatedLocalDoB = LocalDate.parse(patient.getBirthDate());
					Date updatedDoB = Date.from(updatedLocalDoB.atStartOfDay(ZoneId.systemDefault()).toInstant());

					existingPatient.setFirstName(patient.getFirstName());
					existingPatient.setLastName(patient.getLastName());
					existingPatient.setBirthDate(updatedDoB);
					existingPatient.setGender(patient.getGender());
					existingPatient.setAddress(patient.getAddress());
					existingPatient.setPhoneNumber(patient.getPhoneNumber());

					PatientDto savedDto = PatientDto.fromEntity(patientRepository.save(existingPatient));
					log.info("Patient with ID '{}' updated successfully.", id);

					return savedDto;
				})
				.orElseThrow(() -> new PatientIdNotFoundException(id));
	}
}
