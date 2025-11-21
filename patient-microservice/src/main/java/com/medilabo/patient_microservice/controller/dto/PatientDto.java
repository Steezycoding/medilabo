package com.medilabo.patient_microservice.controller.dto;

import com.medilabo.patient_microservice.domain.Patient;
import lombok.Builder;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
				.lastName(patient.getLastName())
				.firstName(patient.getFirstName())
				.birthDate(new SimpleDateFormat("yyyy-MM-dd").format(patient.getBirthDate()))
				.gender(patient.getGender())
				.address(patient.getAddress())
				.phoneNumber(patient.getPhoneNumber())
				.build();
	}

	public static PatientDto fromEntityWithId(Patient patient) {
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

	public Patient toEntity() throws ParseException {
		Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.birthDate);

		return Patient.builder()
				.id(this.id)
				.lastName(this.lastName)
				.firstName(this.firstName)
				.birthDate(birthDate)
				.gender(this.gender)
				.address(this.address)
				.phoneNumber(this.phoneNumber)
				.build();
	}
}
