package com.medilabo.gateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

	private final CorsProperties corsProperties;

	public SecurityFilterChainConfig(CorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

	/**
	 * Configure the security filter chain.
	 * Disables CSRF protection and enables CORS with custom configuration.
	 * Permits all requests to the /actuator/health endpoint.
	 * Requires authentication for all other requests using HTTP Basic authentication.
	 *
	 * @param http HttpSecurity
	 *
	 * @return SecurityFilterChain
	 *
	 * @throws Exception in case of any configuration error
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/actuator/health").permitAll()
						.anyRequest().authenticated()
				)
				.httpBasic(Customizer.withDefaults())
				.build();
	}

	/**
	 * Configure CORS to allow requests from the frontend application.
	 * Allows only specific origins from 8080 port.
	 * Allows common HTTP methods and headers.
	 *
	 * @return CorsConfigurationSource
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOrigins(corsProperties.getAllowed().getOrigins());
		corsConfig.setAllowedMethods(corsProperties.getAllowed().getMethods());
		corsConfig.setAllowedHeaders(corsProperties.getAllowed().getHeaders());
		corsConfig.setAllowCredentials(corsProperties.isAllowedCredentials());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}
}
