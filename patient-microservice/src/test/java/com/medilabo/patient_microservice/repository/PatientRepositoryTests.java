package com.medilabo.patient_microservice.repository;

import com.medilabo.patient_microservice.domain.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("PatientRepository Test Suite")
public class PatientRepositoryTests {
	@Autowired
	private PatientRepository patientRepository;

	@Test
	@DisplayName("findAll() should return all patients")
	public void findAllTest() {
		List<Patient> result = patientRepository.findAll();

		assertThat(result).isNotNull();
		assertThat(result).hasSize(4);
		assertThat(result.get(0).getLastName()).isEqualTo("TestNone");
		assertThat(result.get(1).getLastName()).isEqualTo("TestBorderline");
		assertThat(result.get(2).getLastName()).isEqualTo("TestInDanger");
		assertThat(result.get(3).getLastName()).isEqualTo("TestEarlyOnset");
	}

	@Test
	@DisplayName("findById() should return patient when ID exists")
	public void findByIdTest() {
		Patient result = patientRepository.findById(1L).orElse(null);

		assertThat(result).isNotNull();
		assertThat(result.getLastName()).isEqualTo("TestNone");
	}

	@Test
	@DisplayName("save() should persist a new patient")
	public void saveTest() {
		Patient newPatient = Patient.builder()
				.lastName("TestCreate")
				.firstName("Test")
				.birthDate(Date.valueOf("1990-01-01"))
				.gender("M")
				.address("999 Main St")
				.phoneNumber("999-888-7777")
				.build();
		Patient savedPatient = patientRepository.save(newPatient);
		assertThat(savedPatient.getId()).isNotNull();
		assertThat(savedPatient.getId()).isEqualTo(5L);
		assertThat(savedPatient.getLastName()).isEqualTo("TestCreate");
		assertThat(savedPatient.getFirstName()).isEqualTo("Test");
		assertThat(savedPatient.getBirthDate()).isEqualTo(Date.valueOf("1990-01-01"));
		assertThat(savedPatient.getGender()).isEqualTo("M");
		assertThat(savedPatient.getAddress()).isEqualTo("999 Main St");
		assertThat(savedPatient.getPhoneNumber()).isEqualTo("999-888-7777");
	}
}
