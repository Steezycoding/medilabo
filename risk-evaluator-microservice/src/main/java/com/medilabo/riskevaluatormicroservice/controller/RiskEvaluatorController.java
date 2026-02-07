package com.medilabo.riskevaluatormicroservice.controller;

import com.medilabo.riskevaluatormicroservice.controller.dto.RiskLevelResponse;
import com.medilabo.riskevaluatormicroservice.exception.PatientNotFoundException;
import com.medilabo.riskevaluatormicroservice.service.contracts.RiskEvaluatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/risk-evaluator")
public class RiskEvaluatorController {
	public final RiskEvaluatorService riskEvaluatorService;

	public RiskEvaluatorController(RiskEvaluatorService riskEvaluatorService) {
		this.riskEvaluatorService = riskEvaluatorService;
	}

	@RequestMapping("/patient/{id}")
	public ResponseEntity<RiskLevelResponse> getPatientMedicalNotesTest(@PathVariable Long id) throws Exception {
		log.info("GET /risk-evaluator/patient/{}: Evaluating risk for patient ID '{}'", id, id);
		RiskLevelResponse riskLevel = new RiskLevelResponse(riskEvaluatorService.getRiskLevel(id));
		return ResponseEntity.ok(riskLevel);
	}

	@ExceptionHandler(PatientNotFoundException.class)
	public ResponseEntity<String> handlePatientNotFoundException(PatientNotFoundException ex) {
		log.warn("PatientNotFoundException: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.contentType(MediaType.APPLICATION_JSON)
				.body(ex.getMessage());
	}
}
