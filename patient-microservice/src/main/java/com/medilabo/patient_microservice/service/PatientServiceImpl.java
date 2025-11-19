package com.medilabo.patient_microservice.service;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.exception.PatientIdNotFoundException;
import com.medilabo.patient_microservice.repository.PatientRepository;
import com.medilabo.patient_microservice.service.contracts.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;

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
				.map(PatientDto::fromEntity)
				.toList();
	}

	@Override
	public PatientDto getById(Long patientId) {
		return patientRepository.findById(patientId)
				.map(PatientDto::fromEntity)
				.orElseThrow(() -> new PatientIdNotFoundException(patientId));
	}
}
