package com.medilabo.riskevaluatormicroservice.service;

import com.medilabo.riskevaluatormicroservice.beans.MedicalNoteBean;
import com.medilabo.riskevaluatormicroservice.beans.PatientBean;
import com.medilabo.riskevaluatormicroservice.domain.enums.RiskLevel;
import com.medilabo.riskevaluatormicroservice.domain.enums.TriggerTerm;
import com.medilabo.riskevaluatormicroservice.exception.PatientNotFoundException;
import com.medilabo.riskevaluatormicroservice.proxies.MedicalNoteMicroserviceProxy;
import com.medilabo.riskevaluatormicroservice.proxies.PatientMicroserviceProxy;
import com.medilabo.riskevaluatormicroservice.service.contracts.RiskEvaluatorService;
import com.medilabo.riskevaluatormicroservice.utils.AgeUtils;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiskEvaluatorServiceImpl implements RiskEvaluatorService {

	private final PatientMicroserviceProxy patientMicroserviceProxy;
	private final MedicalNoteMicroserviceProxy medicalNoteMicroserviceProxy;

	public RiskEvaluatorServiceImpl(PatientMicroserviceProxy patientMicroserviceProxy,
									MedicalNoteMicroserviceProxy medicalNoteMicroserviceProxy) {
		this.patientMicroserviceProxy = patientMicroserviceProxy;
		this.medicalNoteMicroserviceProxy = medicalNoteMicroserviceProxy;
	}

	/**
	 * Collects and delegates the necessary data for a patient to calculate his risk level.
	 *
	 * @param patientId The ID of the patient for whom the risk level is to be calculated.
	 *
	 * @return The calculated risk level for the patient.
	 *
	 * @throws PatientNotFoundException If no patient is found with the given ID.
	 */
	@Override
	public RiskLevel getRiskLevel(Long patientId) {
		PatientBean patient;
		try {
			patient = patientMicroserviceProxy.getPatientById(patientId);
			log.debug("Patient with ID '{}' found: {}", patient.getId(), patient);
		} catch (FeignException.NotFound ex) {
			throw new PatientNotFoundException(patientId);
		}

		List<MedicalNoteBean> medicalNotes = medicalNoteMicroserviceProxy.getPatientMedicalNotes(patientId);
		log.debug("Patient Medical Notes found: {}", medicalNotes);

		return evaluate(patient, medicalNotes);
	}

	/**
	 * Evaluates the risk level for a patient based on their medical notes and other relevant data.
	 * <b>WARNING</b>: This method does not handle patient with age exactly 30 years old, as the requirements do not
	 * specify the risk evaluation criteria for this age group.
	 * In such cases, the method will return <em>RiskLevel.NONE</em> by default.
	 *
	 * @param patient      The patient for whom the risk level is to be evaluated.
	 * @param medicalNotes The list of medical notes associated with the patient, which may contain relevant information
	 *                     for risk evaluation.
	 *
	 * @return The calculated risk level for the patient.
	 */
	private RiskLevel evaluate(PatientBean patient, List<MedicalNoteBean> medicalNotes) {
		int patientAge = AgeUtils.calculateAgeFromBirthdate(patient.getBirthDate(), "yyyy-MM-dd");
		String patientGender = patient.getGender();
		log.debug("Patient ID '{}' infos: Age -> {}, Gender -> {}", patient.getId(), patientAge, patientGender);

		if (medicalNotes == null || medicalNotes.isEmpty()) {
			return RiskLevel.NONE;
		}

		int triggerCount = getTriggerCount(medicalNotes);
		log.debug("Calculated triggers for patient ID '{}': {}", patient.getId(), triggerCount);

		if (triggerCount == 0) {
			return RiskLevel.NONE;
		}

		if (patientAge < 30) {
			return evaluateUnder30(patientGender, triggerCount);
		}
		if (patientAge > 30) {
			return evaluateOver30(triggerCount);
		}
		return RiskLevel.NONE;
	}

	/**
	 * Evaluates the risk level for a patient under 30 years old based on their gender and the number of triggers found
	 * in their medical notes.
	 *
	 * @param gender   The gender of the patient
	 * @param triggers The number of triggers found in the patient's medical notes
	 *
	 * @return The calculated risk level for the patient.
	 */
	private RiskLevel evaluateUnder30(String gender, int triggers) {
		if ("M".equalsIgnoreCase(gender)) {
			if (triggers >= 5) return RiskLevel.EARLY_ONSET;
			if (triggers == 3) return RiskLevel.IN_DANGER;
		} else if ("F".equalsIgnoreCase(gender)) {
			if (triggers == 4) return RiskLevel.IN_DANGER;
			if (triggers >= 7) return RiskLevel.EARLY_ONSET;
		}
		return RiskLevel.NONE;
	}

	/**
	 * Evaluates the risk level for a patient over 30 years old based on the number of triggers found in their medical
	 * notes.
	 *
	 * @param triggers The number of triggers found in the patient's medical notes
	 *
	 * @return The calculated risk level for the patient.
	 */
	private RiskLevel evaluateOver30(int triggers) {
		if (triggers > 1 && triggers < 6) return RiskLevel.BORDERLINE;
		if (triggers == 6 || triggers == 7) return RiskLevel.IN_DANGER;
		if (triggers >= 8) return RiskLevel.EARLY_ONSET;
		return RiskLevel.NONE;
	}

	/**
	 * Counts the number of trigger terms found in the medical notes of a patient.
	 *
	 * @param medicalNotes The list of medical notes associated with the patient, which may contain relevant information
	 *                     for risk evaluation.
	 *
	 * @return The count of trigger terms found in the medical notes.
	 */
	private int getTriggerCount(List<MedicalNoteBean> medicalNotes) {
		List<TriggerTerm> triggerTerms = getTriggerTerms(medicalNotes);
		log.debug("Extracted trigger terms from medical notes: {}", triggerTerms);

		return triggerTerms.size();
	}

	/**
	 * Extracts the trigger terms from the medical notes of a patient.
	 * The method processes the medical notes by splitting them into individual words.
	 * It returns a list of unique trigger terms found in the medical notes.
	 *
	 * @param medicalNotes The list of medical notes associated with the patient, which may contain relevant information
	 *                     for risk evaluation.
	 *
	 * @return A list of unique trigger terms found in the medical notes.
	 */
	private List<TriggerTerm> getTriggerTerms(List<MedicalNoteBean> medicalNotes) {
		List<TriggerTerm> terms = Arrays.asList(TriggerTerm.values());
		String noUnicodeRegex = "[^\\p{L}\\p{N}]+"; // Any sequence of non Unicode letters or digits

		return medicalNotes.stream()
				.map(MedicalNoteBean::getNote)
				.filter(Objects::nonNull)
				.flatMap(note -> Arrays.stream(note.split(noUnicodeRegex)))
				.filter(word -> !word.isBlank())
				.map(word -> terms.stream()
						.filter(term -> term.matches(word))
						.findFirst()
						.orElse(null))
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}
}
