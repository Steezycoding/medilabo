package com.medilabo.riskevaluatormicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RiskEvaluatorMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiskEvaluatorMicroserviceApplication.class, args);
	}

}
