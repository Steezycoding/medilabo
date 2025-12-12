package com.medilabo.patient_microservice.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityFilterChainConfig {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtVerifierService jwtVerifierService) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.anyRequest().authenticated()
				)
				.addFilterBefore(gatewayJwtFilter(jwtVerifierService), UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	GatewayJwtFilter gatewayJwtFilter(JwtVerifierService jwtVerifierService) {
		return new GatewayJwtFilter(jwtVerifierService);
	}
}
