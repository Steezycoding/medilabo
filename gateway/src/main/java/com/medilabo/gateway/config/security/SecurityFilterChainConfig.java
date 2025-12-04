package com.medilabo.gateway.config.security;

import com.medilabo.gateway.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

	private final CorsProperties corsProperties;
	private final JwtService jwtService;

	public SecurityFilterChainConfig(CorsProperties corsProperties, JwtService jwtService) {
		this.corsProperties = corsProperties;
		this.jwtService = jwtService;
	}

	/**
	 * Configure the security filter chain for the /auth/token endpoint.
	 * This endpoint requires authentication using HTTP Basic authentication.
	 *
	 * @param http HttpSecurity
	 *
	 * @return SecurityFilterChain
	 *
	 * @throws Exception in case of any configuration error
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain authTokenFilterChain(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/auth/token")
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.anyRequest().authenticated()
				)
				.httpBasic(Customizer.withDefaults())
				.build();
	}

	/**
	 * Configure the security filter chain for all API endpoints.
	 * Before accessing any API endpoint, the JWT authentication filter is applied.
	 * All API endpoints require authentication using JWT tokens.
	 *
	 * @param http HttpSecurity
	 *
	 * @return SecurityFilterChain
	 *
	 * @throws Exception in case of any configuration error
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);

		return http
				.securityMatcher("/api/**")
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	/**
	 * Configure the global security filter chain.
	 * This chain applies to all other requests not matched by previous chains.
	 * It denies all requests except for excluded endpoints in permitting matchers.
	 *
	 * @param http HttpSecurity
	 *
	 * @return SecurityFilterChain
	 *
	 * @throws Exception in case of any configuration error
	 */
	@Bean
	@Order(3)
	public SecurityFilterChain globalFilterChain(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/**")
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/actuator/health").permitAll()
						.anyRequest().denyAll()
				)
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
		corsConfig.setAllowCredentials(corsProperties.isCredentials());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}
}
