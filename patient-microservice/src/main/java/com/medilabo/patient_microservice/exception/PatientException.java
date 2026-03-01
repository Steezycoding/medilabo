package com.medilabo.patient_microservice.exception;

public class PatientException extends RuntimeException {
	public PatientException(String message) {
		super(message);
	}
}
