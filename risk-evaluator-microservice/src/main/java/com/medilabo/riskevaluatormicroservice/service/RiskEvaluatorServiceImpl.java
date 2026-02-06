package com.medilabo.riskevaluatormicroservice.service;

import com.medilabo.riskevaluatormicroservice.beans.PatientBean;
import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;
import com.medilabo.riskevaluatormicroservice.exception.PatientNotFoundException;
import com.medilabo.riskevaluatormicroservice.proxies.PatientMicroserviceProxy;
import com.medilabo.riskevaluatormicroservice.service.contracts.RiskEvaluatorService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RiskEvaluatorServiceImpl implements RiskEvaluatorService {

	private final PatientMicroserviceProxy patientMicroserviceProxy;

	public RiskEvaluatorServiceImpl(PatientMicroserviceProxy patientMicroserviceProxy) {
		this.patientMicroserviceProxy = patientMicroserviceProxy;
	}

	private final Map<Long, RiskLevel> riskPatientLevels = Map.of(
			1L, RiskLevel.NONE,
			2L, RiskLevel.BORDERLINE,
			3L, RiskLevel.IN_DANGER,
			4L, RiskLevel.EARLY_ONSET
	);

	@Override
	public RiskLevel evaluate(Long patientId) {
		PatientBean patient;
		try {
			patient = patientMicroserviceProxy.getPatientById(patientId);
			log.debug("Patient with ID '{}' found: {}", patient.getId(), patient);
		} catch (FeignException.NotFound ex) {
			throw new PatientNotFoundException(patientId);
		}

		return riskPatientLevels.get(patient.getId());
	}
}
