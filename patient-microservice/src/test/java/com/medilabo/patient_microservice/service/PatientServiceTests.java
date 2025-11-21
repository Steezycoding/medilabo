package com.medilabo.patient_microservice.service;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import com.medilabo.patient_microservice.domain.Patient;
import com.medilabo.patient_microservice.exception.PatientException;
import com.medilabo.patient_microservice.exception.PatientIdNotFoundException;
import com.medilabo.patient_microservice.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static com.medilabo.patient_microservice.utils.DateUtils.createDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Test Suite")
public class PatientServiceTests {

	@Mock
	private PatientRepository patientRepository;

	@InjectMocks
	private PatientServiceImpl patientService;

	@Nested
	@DisplayName("getAll() Tests")
	class GetAllTests {
		@Test
		@DisplayName("Should return patients")
		public void givenPatientsList_whenGetAll_thenReturnPatients() {
			List<Patient> patients = List.of(
					new Patient(1L, "Doe", "John", createDate(1966, Month.DECEMBER, 31), "M", "1 Brookside St", "111-222-3333"),
					new Patient(2L, "Smith", "Jane", createDate(1974, Month.JUNE, 24), "F", "20 Club Road", "444-555-6666")
			);

			when(patientRepository.findAll()).thenReturn(patients);

			List<PatientDto> result = patientService.getAll();

			assertThat(result).hasSize(2);
			assertThat(result.get(0).getId()).isEqualTo(1L);
			assertThat(result.get(1).getId()).isEqualTo(2L);

			verify(patientRepository).findAll();
			verifyNoMoreInteractions(patientRepository);
		}
	}

	@Nested
	@DisplayName("getById() Tests")
	class GetByIdTests {
		@Test
		@DisplayName("Should return patient if ID is found")
		public void givenPatientId_whenGetById_thenReturnPatient() {
			Patient patient = new Patient(2L, "Smith", "Jane", createDate(1974, Month.JUNE, 24), "F", "20 Club Road", "444-555-6666");

			when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));

			PatientDto result = patientService.getById(2L);

			assertThat(result).isNotNull();
			assertThat(result.getId()).isNull();
			assertThat(result).isEqualTo(PatientDto.fromEntity(patient));

			verify(patientRepository).findById(eq(2L));
			verifyNoMoreInteractions(patientRepository);
		}

		@Test
		@DisplayName("Should throw PatientIdNotFoundException if ID NOT found")
		public void givenInvalidPatientId_whenGetById_thenThrowException() {
			Long noExistentPatientId = -1L;

			when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

			PatientException exception = assertThrows(PatientIdNotFoundException.class, () -> {
				patientService.getById(noExistentPatientId);
			});

			String expectedExceptionMessage = String.format("Patient with ID '%d' doesn't exist.", noExistentPatientId);

			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
			verify(patientRepository).findById(eq(noExistentPatientId));
			verifyNoMoreInteractions(patientRepository);
		}
	}

	@Nested
	@DisplayName("update() Tests")
	class UpdateTests {
		@Test
		@DisplayName("Should update patient & return updated patient")
		public void givenPatientDto_whenUpdate_thenReturnUpdatedPatientDto() {
			Patient existingPatient = new Patient(2L, "Smith", "Jane", createDate(1974, Month.JUNE, 24), "F", "20 Club Road", "444-555-6666");
			Patient updatedPatient = new Patient(2L, "Smith", "Updated First Name", createDate(1974, Month.JUNE, 24), "F", "20 Updated Address", "444-555-6666");

			when(patientRepository.findById(anyLong())).thenReturn(Optional.of(existingPatient));
			when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

			PatientDto result = patientService.update(existingPatient.getId(), PatientDto.fromEntity(updatedPatient));

			assertThat(result).isNotNull();
			assertThat(result.getLastName()).isEqualTo(existingPatient.getLastName());
			assertThat(result.getFirstName()).isEqualTo("Updated First Name");
			assertThat(result.getBirthDate()).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").format(existingPatient.getBirthDate()));
			assertThat(result.getAddress()).isEqualTo("20 Updated Address");
			assertThat(result.getGender()).isEqualTo(existingPatient.getGender());
			assertThat(result.getPhoneNumber()).isEqualTo(existingPatient.getPhoneNumber());

			verify(patientRepository).findById(eq(existingPatient.getId()));
			verify(patientRepository).save(eq(updatedPatient));
			verifyNoMoreInteractions(patientRepository);
		}

		@Test
		@DisplayName("Should throw PatientIdNotFoundException if ID NOT found")
		public void givenInvalidPatientId_whenUpdate_thenThrowException() {
			Long noExistentPatientId = -1L;

			when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

			PatientException exception = assertThrows(PatientIdNotFoundException.class, () -> {
				patientService.update(noExistentPatientId, any(PatientDto.class));
			});

			String expectedExceptionMessage = String.format("Patient with ID '%d' doesn't exist.", noExistentPatientId);

			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
			verify(patientRepository).findById(eq(noExistentPatientId));
			verifyNoMoreInteractions(patientRepository);
		}
	}

	@Nested
	@DisplayName("create() Tests")
	class CreateTests {
		@Test
		@DisplayName("Should create patient & return created patient")
		public void givenPatientDto_whenCreate_thenReturnCreatedPatientDto() throws ParseException {
			PatientDto newPatient = PatientDto.builder()
					.lastName("Brown")
					.firstName("Charlie")
					.birthDate("1980-05-15")
					.gender("M")
					.address("50 Pine St")
					.phoneNumber("777-888-9999")
					.build();

			Patient createdPatient = Patient.builder()
					.id(3L)
					.lastName(newPatient.getLastName())
					.firstName(newPatient.getFirstName())
					.birthDate(new SimpleDateFormat("yyyy-MM-dd").parse(newPatient.getBirthDate()))
					.gender(newPatient.getGender())
					.address(newPatient.getAddress())
					.phoneNumber(newPatient.getPhoneNumber())
					.build();

			when(patientRepository.save(any(Patient.class))).thenReturn(createdPatient);

			PatientDto result = patientService.create(newPatient);

			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(3L);
			assertThat(result.getLastName()).isEqualTo("Brown");
			assertThat(result.getFirstName()).isEqualTo("Charlie");
			assertThat(result.getBirthDate()).isEqualTo("1980-05-15");
			assertThat(result.getGender()).isEqualTo("M");
			assertThat(result.getAddress()).isEqualTo("50 Pine St");
			assertThat(result.getPhoneNumber()).isEqualTo("777-888-9999");

			verify(patientRepository).save(eq(newPatient.toEntity()));
			verifyNoMoreInteractions(patientRepository);
		}
	}
}
