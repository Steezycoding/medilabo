package com.medilabo.patient_microservice.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientDto {
	private String lastName;
	private String firstName;
	private String birthDate;
	private String gender;
	private String address;
	private String phoneNumber;
}
