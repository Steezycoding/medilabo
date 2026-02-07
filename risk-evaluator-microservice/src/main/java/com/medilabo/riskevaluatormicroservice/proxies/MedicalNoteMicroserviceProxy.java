package com.medilabo.riskevaluatormicroservice.proxies;

import com.medilabo.riskevaluatormicroservice.beans.MedicalNoteBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "medical-note-microservice")
public interface MedicalNoteMicroserviceProxy {

	@GetMapping("/medical-notes/patient/{id}")
	List<MedicalNoteBean> getPatientMedicalNotes(@PathVariable("id") long id);
}
