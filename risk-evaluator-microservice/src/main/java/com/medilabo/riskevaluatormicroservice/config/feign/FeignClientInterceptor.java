package com.medilabo.riskevaluatormicroservice.config.feign;

import com.medilabo.riskevaluatormicroservice.config.security.JwtContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		String token = JwtContextHolder.getToken();

		if (token != null) {
			template.header("Cookie", "access_token=" + token);
		}
	}
}



