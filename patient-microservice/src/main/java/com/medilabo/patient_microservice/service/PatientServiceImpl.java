package com.medilabo.patient_microservice.service;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.domain.Patient;
import com.medilabo.patient_microservice.exception.PatientIdNotFoundException;
import com.medilabo.patient_microservice.repository.PatientRepository;
import com.medilabo.patient_microservice.service.contracts.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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

	/**
	 * Retrieves all patients from the database and converts them to a list of PatientDto objects.
	 *
	 * @return A list of PatientDto objects representing all patients in the database.
	 */
	@Override
	public List<PatientDto> getAll() {
		return patientRepository.findAll()
				.stream()
				.map(PatientDto::fromEntityWithId)
				.toList();
	}

	/**
	 * Retrieves a patient by their ID. If the patient is found, it is converted to a PatientDto object and returned.
	 * If the patient is not found, a PatientIdNotFoundException is thrown.
	 *
	 * @param patientId The ID of the patient to be retrieved.
	 *
	 * @return A PatientDto object representing the patient with the specified ID.
	 *
	 * @throws PatientIdNotFoundException if no patient with the specified ID exists in the database.
	 */
	@Override
	public PatientDto getById(Long patientId) {
		return patientRepository.findById(patientId)
				.map(PatientDto::fromEntityWithId)
				.orElseThrow(() -> new PatientIdNotFoundException(patientId));
	}

	/**
	 * Updates an existing patient with the provided details. If the patient with the specified ID exists, it is updated
	 * with the new details and saved to the database.
	 * If the patient with the specified ID does not exist, a PatientIdNotFoundException is thrown.
	 *
	 * @param id      The ID of the patient to be updated.
	 * @param patient A PatientDto object containing the new details for the patient.
	 *
	 * @return A PatientDto object representing the updated patient.
	 *
	 * @throws PatientIdNotFoundException if no patient with the specified ID exists in the database.
	 */
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

	/**
	 * Creates a new patient in the database using the details provided in the PatientDto object.
	 *
	 * @param patientDto A PatientDto object containing the details of the patient to be created.
	 *
	 * @return A PatientDto object representing the newly created patient, including its generated ID.
	 *
	 * @throws ParseException if there is an error parsing the birthdate from the PatientDto.
	 */
	@Override
	public PatientDto create(PatientDto patientDto) throws ParseException {
		Patient createdPatientEntity = patientRepository.save(patientDto.toEntity());

		PatientDto createdPatientDto = PatientDto.fromEntityWithId(createdPatientEntity);
		log.info("Patient created with ID '{}'.", createdPatientDto.getId());

		return createdPatientDto;
	}
}
