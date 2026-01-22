package com.medilabo.medicalnotemicroservice;

import com.jayway.jsonpath.JsonPath;
import com.medilabo.medicalnotemicroservice.controller.dto.MedicalNoteDto;
import com.medilabo.medicalnotemicroservice.utils.MongoTestDataHelper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.medilabo.medicalnotemicroservice.utils.JsonUtils.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MedicalNoteIntegrationTest {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		if (!mongoDBContainer.isRunning()) {
			mongoDBContainer.start();
		}
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	void beforeAll() {
		MongoTestDataHelper.loadSeed(mongoTemplate);
	}

	@AfterAll
	void afterAll() {
		MongoTestDataHelper.clear(mongoTemplate);
	}

	@Nested
	@DisplayName("ENDPOINT Tests")
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class MedicalNoteApiIntegrationTests {

		/**
		 * Test constants from test seed data (data-test.json)
		 */
		private static final Integer PATIENT_ID_UNDER_TEST = 2;
		private static final String PATIENT_NAME_UNDER_TEST = "TestBorderline";
		private static final String NOTE_1_UNDER_TEST = "Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement";
		private static final String NOTE_2_UNDER_TEST = "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale";
		private static final String NOTE_NEW_UNDER_TEST = "Le patient signale des douleurs thoraciques occasionnelles et une fatigue accrue ces dernières semaines.";

		private static String newNoteId;

		@Test
		@Order(1)
		@DisplayName("GET /medical-notes/patient/{id} with data should return all medical notes for patient")
		public void getMedicalNotesByPatientId_shouldReturnOkAndJsonFromDb() throws Exception {
			mockMvc.perform(get("/medical-notes/patient/{id}", PATIENT_ID_UNDER_TEST))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(2))
					.andExpect(jsonPath("$[0].patId").value(PATIENT_ID_UNDER_TEST))
					.andExpect(jsonPath("$[0].patient").value(PATIENT_NAME_UNDER_TEST))
					.andExpect(jsonPath("$[0].note").value(NOTE_1_UNDER_TEST))
					.andExpect(jsonPath("$[1].patId").value(PATIENT_ID_UNDER_TEST))
					.andExpect(jsonPath("$[1].patient").value(PATIENT_NAME_UNDER_TEST))
					.andExpect(jsonPath("$[1].note").value(NOTE_2_UNDER_TEST));
		}

		@Test
		@Order(2)
		@DisplayName("POST /medical-notes with data should create a new medical note")
		public void createMedicalNote_shouldReturnCreatedAndJsonFromDb() throws Exception {
			MedicalNoteDto newMedicalNote = MedicalNoteDto.builder()
					.patId(2)
					.patient("TestBorderline")
					.note("Le patient signale des douleurs thoraciques occasionnelles et une fatigue accrue ces dernières semaines.")
					.build();

			MvcResult mvcResult = mockMvc.perform(post("/medical-notes")
							.contentType(MediaType.APPLICATION_JSON)
							.content(asJsonString(newMedicalNote)))
					.andExpect(status().isCreated())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").isString())
					.andExpect(jsonPath("$.patId").value(PATIENT_ID_UNDER_TEST))
					.andExpect(jsonPath("$.patient").value(PATIENT_NAME_UNDER_TEST))
					.andExpect(jsonPath("$.note").value(NOTE_NEW_UNDER_TEST))
					.andReturn();

			// Get the ID of the created note for deletion in later test
			String responseJson = mvcResult.getResponse().getContentAsString();
			newNoteId = JsonPath.read(responseJson, "$.id");
		}

		@Test
		@Order(3)
		@DisplayName("GET /medical-notes/patient/{id} should return all medical notes for patient with new note added")
		public void getMedicalNotesByPatientId_shouldReturnOkAndNewNoteAdded() throws Exception {
			mockMvc.perform(get("/medical-notes/patient/{id}", PATIENT_ID_UNDER_TEST))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(3))
					.andExpect(jsonPath("$[2].id").value(newNoteId))
					.andExpect(jsonPath("$[2].patId").value(PATIENT_ID_UNDER_TEST))
					.andExpect(jsonPath("$[2].patient").value(PATIENT_NAME_UNDER_TEST))
					.andExpect(jsonPath("$[2].note").value(NOTE_NEW_UNDER_TEST));
		}

		@Test
		@Order(4)
		@DisplayName("DELETE /medical-notes/{id} should delete the medical note with given id")
		public void deleteMedicalNote_shouldReturnOkAndDeletedId() throws Exception {
			mockMvc.perform(delete("/medical-notes/{id}", newNoteId))
					.andExpect(status().isOk())
					.andExpect(content().string("Deleted medical note with ID: "
							+ newNoteId));
		}

		@Test
		@Order(5)
		@DisplayName("GET /medical-notes/patient/{id} should return all medical notes for patient with new note deleted")
		public void getMedicalNotesByPatientId_shouldReturnOkAndNewNoteDeleted() throws Exception {
			mockMvc.perform(get("/medical-notes/patient/{id}", PATIENT_ID_UNDER_TEST))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(2))
					.andExpect(jsonPath("$[2].id").doesNotExist());
		}
	}
}
