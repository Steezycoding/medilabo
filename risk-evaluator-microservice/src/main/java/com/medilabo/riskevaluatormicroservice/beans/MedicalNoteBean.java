package com.medilabo.riskevaluatormicroservice.beans;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MedicalNoteBean {
	private String id;
	private Integer patId;
	private String patient;
	private String note;
	private Date createdAt;
}
