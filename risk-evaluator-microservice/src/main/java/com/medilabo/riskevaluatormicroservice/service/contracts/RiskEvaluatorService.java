package com.medilabo.riskevaluatormicroservice.service.contracts;

import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;

public interface RiskEvaluatorService {
	/**
	 * Evaluates the risk level for a patient based on their medical notes and other relevant data.
	 *
	 * @param patientId The ID of the patient for whom the risk level is to be evaluated.
	 *
	 * @return The calculated risk level for the patient.
	 */
	RiskLevel evaluate(Long patientId);
}
