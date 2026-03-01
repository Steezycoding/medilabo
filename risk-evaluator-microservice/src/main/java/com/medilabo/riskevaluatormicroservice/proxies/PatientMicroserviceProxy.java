package com.medilabo.riskevaluatormicroservice.proxies;

import com.medilabo.riskevaluatormicroservice.beans.PatientBean;
import com.medilabo.riskevaluatormicroservice.config.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-microservice", configuration = FeignClientConfig.class)
public interface PatientMicroserviceProxy {

	@GetMapping("/patients/{id}")
	PatientBean getPatientById(@PathVariable("id") long id);
}
