package com.medilabo.medicalnotemicroservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "notes")
public class MedicalNote {
	@Id
	private String id;
	private Integer patId;
	private String patient;
	private String note;
}
