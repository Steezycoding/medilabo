package com.medilabo.patient_microservice.service.contracts;

import com.medilabo.patient_microservice.controller.dto.PatientDto;

import java.text.ParseException;
import java.util.List;

public interface PatientService {
	List<PatientDto> getAll();

	PatientDto getById(Long patientId);

	PatientDto update(Long id, PatientDto patient);

	PatientDto create(PatientDto patientDto) throws ParseException;
}
