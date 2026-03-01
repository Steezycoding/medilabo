package com.medilabo.medicalnotemicroservice.repository;

import com.medilabo.medicalnotemicroservice.domain.MedicalNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalNoteRepository extends MongoRepository<MedicalNote, String> {
	List<MedicalNote> findAllByPatIdOrderByCreatedAtDesc(Integer patId);

	void deleteMedicalNoteById(String id);
}
