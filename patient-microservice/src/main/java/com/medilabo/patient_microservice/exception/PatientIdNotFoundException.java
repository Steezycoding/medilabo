package com.medilabo.patient_microservice.exception;

public class PatientIdNotFoundException extends PatientException {
	public PatientIdNotFoundException(Long patientId) {
		super(String.format("Patient with ID '%d' doesn't exist.", patientId));
	}
}
