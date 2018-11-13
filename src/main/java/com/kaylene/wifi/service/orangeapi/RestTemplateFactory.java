package com.kaylene.wifi.service.orangeapi;

import org.apache.http.HttpHost;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory {

//@Component
//public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {
//	private RestTemplate restTemplate;
//
//	public RestTemplateFactory() {
//		super();
//	}
//
//	// API
//
//	@Override
//	public RestTemplate getObject() {
//		return restTemplate;
//	}
//
//	@Override
//	public Class<RestTemplate> getObjectType() {
//		return RestTemplate.class;
//	}
//
//	@Override
//	public boolean isSingleton() {
//		return true;
//	}
//
//	@Override
//	public void afterPropertiesSet() {
//		System.out.println("afterPropertiesSet");
//		// HttpHost host = new HttpHost("api.orange.com/oauth/v2/token", 80, "https");
//		HttpHost host = new HttpHost("https://api.orange.com/oauth/v2/token");
//		final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(host);
//		restTemplate = new RestTemplate(requestFactory);
//		restTemplate.getInterceptors()
//				.add(new BasicAuthorizationInterceptor("p7Wpjkx46BdTkdtZAZFAWth101bjOLFi", "H7wVGMHECvsSUBfP"));
//	}

}
