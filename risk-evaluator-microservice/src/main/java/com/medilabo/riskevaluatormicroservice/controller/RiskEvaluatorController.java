package com.medilabo.riskevaluatormicroservice.controller;

import com.medilabo.riskevaluatormicroservice.controller.dto.RiskLevelResponse;
import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/risk-evaluator")
public class RiskEvaluatorController {

	private final Map<Long, RiskLevel> riskPatientLevels = Map.of(
			1L, RiskLevel.NONE,
			2L, RiskLevel.BORDERLINE,
			3L, RiskLevel.IN_DANGER,
			4L, RiskLevel.EARLY_ONSET
	);

	@RequestMapping("/patient/{id}")
	public ResponseEntity<RiskLevelResponse> getPatientMedicalNotesTest(@PathVariable Long id) throws Exception {
		RiskLevelResponse riskLevel = new RiskLevelResponse(riskPatientLevels.get(id));
		return ResponseEntity.ok(riskLevel);
	}
}
