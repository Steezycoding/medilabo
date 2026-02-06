package com.medilabo.riskevaluatormicroservice.proxies;

import com.medilabo.riskevaluatormicroservice.beans.PatientBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-microservice")
public interface PatientMicroserviceProxy {

	@GetMapping("/patients/{id}")
	PatientBean getPatientById(@PathVariable("id") long id);
}
