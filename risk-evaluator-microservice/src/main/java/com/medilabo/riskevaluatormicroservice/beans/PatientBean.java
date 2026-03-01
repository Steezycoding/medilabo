package com.medilabo.riskevaluatormicroservice.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientBean {
	private Long id;
	private String lastName;
	private String firstName;
	private String birthDate;
	private String gender;
	private String address;
	private String phoneNumber;
}
