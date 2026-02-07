package com.medilabo.riskevaluatormicroservice.service.contracts;

import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;

public interface RiskEvaluatorService {
	/**
	 * Retrieves the risk level for a patient based on their ID.
	 *
	 * @param patientId
	 *
	 * @return The risk level of the patient.
	 */
	RiskLevel getRiskLevel(Long patientId);
}
