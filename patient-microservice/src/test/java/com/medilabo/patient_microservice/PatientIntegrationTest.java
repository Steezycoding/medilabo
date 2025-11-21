package com.medilabo.patient_microservice;

import com.medilabo.patient_microservice.controller.dto.PatientDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.medilabo.patient_microservice.utils.JsonUtils.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Patient API Integration Test Suite")
public class PatientIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("ENDPOINT Tests")
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class PatientApiIntegrationTests {
		@Test
		@Order(1)
		@DisplayName("GET /patients with data should return all patients")
		public void getAllPatients_shouldReturnOkAndJsonFromDb() throws Exception {
			mockMvc.perform(get("/patients"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(4))
					.andExpect(jsonPath("$[0].lastName").value("TestNone"))
					.andExpect(jsonPath("$[0].firstName").value("Test"))
					.andExpect(jsonPath("$[0].birthDate").value("1966-12-31"))
					.andExpect(jsonPath("$[0].gender").value("F"))
					.andExpect(jsonPath("$[0].address").value("1 Brookside St"))
					.andExpect(jsonPath("$[0].phoneNumber").value("100-222-3333"))
					.andExpect(jsonPath("$[1].lastName").value("TestBorderline"))
					.andExpect(jsonPath("$[2].lastName").value("TestInDanger"))
					.andExpect(jsonPath("$[3].lastName").value("TestEarlyOnset"));
		}

		@Test
		@Order(2)
		@DisplayName("GET /patients/{id} with data should return a patient")
		public void getPatientById_shouldReturnOkAndJsonFromDb() throws Exception {
			mockMvc.perform(get("/patients/{id}", 1L))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.lastName").value("TestNone"))
					.andExpect(jsonPath("$.firstName").value("Test"))
					.andExpect(jsonPath("$.birthDate").value("1966-12-31"))
					.andExpect(jsonPath("$.gender").value("F"))
					.andExpect(jsonPath("$.address").value("1 Brookside St"))
					.andExpect(jsonPath("$.phoneNumber").value("100-222-3333"));
		}

		@Test
		@Order(3)
		@DisplayName("PUT /patients/{id} with data should update a patient")
		public void updatePatientById_shouldReturnOkAndJsonFromDb() throws Exception {
			PatientDto updatedPatient = PatientDto.builder()
					.lastName("TestNone")
					.firstName("Updated First Name")
					.birthDate("1966-12-31")
					.gender("F")
					.address("Updated Address")
					.phoneNumber("100-222-3333")
					.build();

			mockMvc.perform(put("/patients/{id}", 1L)
							.contentType(MediaType.APPLICATION_JSON)
							.content(asJsonString(updatedPatient)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.lastName").value("TestNone"))
					.andExpect(jsonPath("$.firstName").value("Updated First Name"))
					.andExpect(jsonPath("$.birthDate").value("1966-12-31"))
					.andExpect(jsonPath("$.gender").value("F"))
					.andExpect(jsonPath("$.address").value("Updated Address"))
					.andExpect(jsonPath("$.phoneNumber").value("100-222-3333"));
		}

		@Test
		@Order(4)
		@DisplayName("POST /patients with data should create a new patient")
		public void createPatient_shouldReturnCreatedAndJsonFromDb() throws Exception {
			PatientDto newPatient = PatientDto.builder()
					.lastName("TestCreatePatient")
					.firstName("Test")
					.birthDate("2025-10-22")
					.gender("F")
					.address("999 New St")
					.phoneNumber("999-888-7777")
					.build();

			mockMvc.perform(post("/patients")
							.contentType(MediaType.APPLICATION_JSON)
							.content(asJsonString(newPatient)))
					.andExpect(status().isCreated())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").value("5"))
					.andExpect(jsonPath("$.lastName").value("TestCreatePatient"))
					.andExpect(jsonPath("$.firstName").value("Test"))
					.andExpect(jsonPath("$.birthDate").value("2025-10-22"))
					.andExpect(jsonPath("$.gender").value("F"))
					.andExpect(jsonPath("$.address").value("999 New St"))
					.andExpect(jsonPath("$.phoneNumber").value("999-888-7777"));
		}

		@Test
		@Order(5)
		@DisplayName("GET /patients Check Integrity (with updated & created)")
		public void getAllPatients_shouldReturnOkAndJsonFromDbWithUpdatedCreated() throws Exception {
			mockMvc.perform(get("/patients"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(5))
					.andExpect(jsonPath("$[0].lastName").value("TestNone"))
					.andExpect(jsonPath("$[0].firstName").value("Updated First Name"))
					.andExpect(jsonPath("$[0].address").value("Updated Address"))
					.andExpect(jsonPath("$[1].lastName").value("TestBorderline"))
					.andExpect(jsonPath("$[2].lastName").value("TestInDanger"))
					.andExpect(jsonPath("$[3].lastName").value("TestEarlyOnset"))
					.andExpect(jsonPath("$[4].lastName").value("TestCreatePatient"));
		}

		@Test
		@Order(6)
		@Sql(statements = "DELETE FROM patient")
		@DisplayName("GET /patients with NO data should return No Content")
		void getAllPatients_whenNoData_shouldReturnNoContent() throws Exception {
			mockMvc.perform(get("/patients"))
					.andExpect(status().isNoContent())
					.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));
		}
	}
}
