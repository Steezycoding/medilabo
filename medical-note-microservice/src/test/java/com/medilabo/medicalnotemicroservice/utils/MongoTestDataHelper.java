package com.medilabo.medicalnotemicroservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.InputStream;

public class MongoTestDataHelper {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Load seed data from data-test.json ("test" classpath) into the MongoDB test database.
	 *
	 * @param mongoTemplate the MongoTemplate to interact with the database
	 */
	public static void loadSeed(MongoTemplate mongoTemplate) {
		try {
			mongoTemplate.getDb().drop();

			ClassPathResource resource = new ClassPathResource("data-test.json");
			try (InputStream is = resource.getInputStream()) {
				JsonNode root = MAPPER.readTree(is);
				if (root.isArray()) {
					for (JsonNode node : root) {
						Document doc = Document.parse(node.toString());
						mongoTemplate.insert(doc, "notes");
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to init Test Database", e);
		}
	}

	/**
	 * Clear the MongoDB test database.
	 *
	 * @param mongoTemplate the MongoTemplate to interact with the database
	 */
	public static void clear(MongoTemplate mongoTemplate) {
		mongoTemplate.getDb().drop();
	}
}
