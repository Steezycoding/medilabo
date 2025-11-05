package com.medilabo.patient_microservice.repository;

import com.medilabo.patient_microservice.domain.Patient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Month;
import java.util.List;

import static com.medilabo.patient_microservice.utils.DateUtils.createDate;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PatientRepositoryTests {
	@Autowired
	private PatientRepository patientRepository;

	@BeforeAll
	public static void setup(@Autowired PatientRepository patientRepository) {
		patientRepository.save(new Patient(null, "TestNone", "Test", createDate(1966, Month.DECEMBER, 31), "F", "1 Brookside St", "100-222-3333"));
		patientRepository.save(new Patient(null, "TestBorderline", "Test", createDate(1945, Month.JULY, 24), "M", "2 High St", "200-333-4444"));
		patientRepository.save(new Patient(null, "TestInDanger", "Test", createDate(2004, Month.JULY, 18), "M", "3 Club Road", "300-444-5555"));
		patientRepository.save(new Patient(null, "TestEarlyOnset", "Test", createDate(2002, Month.JULY, 28), "F", "4 Valley Dr", "400-555-6666"));
	}

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
