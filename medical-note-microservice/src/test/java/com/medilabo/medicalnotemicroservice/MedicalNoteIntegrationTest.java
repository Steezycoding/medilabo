package com.medilabo.medicalnotemicroservice;

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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
		@Test
		@Order(1)
		@DisplayName("GET /medical-notes/patient/{id} with data should return all medical notes for patient")
		public void getMedicalNotesByPatientId_shouldReturnOkAndJsonFromDb() throws Exception {
			mockMvc.perform(get("/medical-notes/patient/{id}", 2))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(2))
					.andExpect(jsonPath("$[0].patId").value(2))
					.andExpect(jsonPath("$[0].patient").value("TestBorderline"))
					.andExpect(jsonPath("$[0].note").value("Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement"))
					.andExpect(jsonPath("$[1].patId").value(2))
					.andExpect(jsonPath("$[1].patient").value("TestBorderline"))
					.andExpect(jsonPath("$[1].note").value("Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale"));
		}
	}
}
