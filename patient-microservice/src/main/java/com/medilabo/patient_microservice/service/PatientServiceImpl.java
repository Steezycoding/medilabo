package com.medilabo.patient_microservice.service;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.service.contracts.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

	@Override
	public List<PatientDto> getAll() {
		return List.of();
	}
}
