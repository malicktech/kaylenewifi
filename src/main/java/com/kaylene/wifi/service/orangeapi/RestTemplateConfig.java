package com.kaylene.wifi.service.orangeapi;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	public final static String GET_TOKEN_URL = "https://api.orange.com/oauth/v2/token";
	public final static String GET_TOKEN_AUTHORIZATION_HEADER = "Basic cDdXcGpreDQ2QmRUa2R0WkFaRkFXdGgxMDFiak9MRmk6SDd3VkdNSEVDdnNTVUJmUA==";
	private static final String CLIENT_ID = "p7Wpjkx46BdTkdtZAZFAWth101bjOLFi";
	private static final String SECRET_ID = "H7wVGMHECvsSUBfP";
	
	@Bean
    public RestTemplate myRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(GET_TOKEN_URL)
                .basicAuthorization(CLIENT_ID, SECRET_ID)
                .build();
    }
	
}
