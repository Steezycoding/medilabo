package com.medilabo.patient_microservice.controller.dto;

import com.medilabo.patient_microservice.domain.Patient;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;

@Data
@Builder
public class PatientDto {
	private Long id;
	private String lastName;
	private String firstName;
	private String birthDate;
	private String gender;
	private String address;
	private String phoneNumber;

	public static PatientDto fromEntity(Patient patient) {
		return PatientDto.builder()
				.id(patient.getId())
				.lastName(patient.getLastName())
				.firstName(patient.getFirstName())
				.birthDate(new SimpleDateFormat("yyyy-MM-dd").format(patient.getBirthDate()))
				.gender(patient.getGender())
				.address(patient.getAddress())
				.phoneNumber(patient.getPhoneNumber())
				.build();
	}
}
