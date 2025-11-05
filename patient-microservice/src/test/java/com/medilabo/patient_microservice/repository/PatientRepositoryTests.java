package com.medilabo.patient_microservice.repository;

import com.medilabo.patient_microservice.domain.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

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
}
