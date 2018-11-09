package com.kaylene.wifi.service.orangeapi;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.kaylene.wifi.config.Constants;

@Service
public class OrangeSmsApiService {

	private final static Logger log = LoggerFactory.getLogger(OrangeSmsApiService.class);

	private final RestTemplate restTemplate;

	public OrangeSmsApiService(RestTemplateBuilder RestTemplateBuilder) {
		this.restTemplate = RestTemplateBuilder.build();
	}
	
	public String getAccesToken() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", Constants.GET_TOKEN_AUTHORIZATION_HEADER_test);

		log.info("Sending getToken request to Orange SMS API");

		HttpEntity<String> entity = new HttpEntity<String>(Constants.GET_TOKEN_REQUEST_BODY, headers);
		try {
			Token response = restTemplate.postForObject(Constants.GET_TOKEN_URL, entity, Token.class);
			String accessToken = response.getAccess_token();
			return accessToken;
		} catch (Exception e) {
			log.error("Failed to get access token from Orange SMS API");
			return null;
		}
	}

	public void sendSms(String phoneNumber, String message) {
		log.debug("REST request to save Record : {} / {}", phoneNumber, message);

		// taille maximale d'un SMS: 140 caractères, si le message dépasse cette limite,
		// prendre les 160 premiers caractères
		if (message.length() > 140) {
			message = message.substring(0, 140);
		}

		JSONObject requestBody = new JSONObject();
		JSONObject outboundSMSMessageRequest = new JSONObject();
		JSONObject outboundSMSTextMessage = new JSONObject();

		try {
			outboundSMSTextMessage.put("message", message);
			outboundSMSMessageRequest.put("address", "tel:+221" + phoneNumber);
			outboundSMSMessageRequest.put("senderAddress", Constants.SMS_SENDER_ADDRESS);
			outboundSMSMessageRequest.put("senderName", Constants.SMS_SENDER_NAME);
			outboundSMSMessageRequest.put("outboundSMSTextMessage", outboundSMSTextMessage);
			requestBody.put("outboundSMSMessageRequest", outboundSMSMessageRequest);
			log.debug(requestBody.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer" + " " + getAccesToken());

		// ******** Build URI from URL to prevent restTemplate of encoding it ******
		String url = Constants.SEND_SMS_URL;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		UriComponents components = builder.build(true);
		URI uri = components.toUri();
		// **************************************************************************

		HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);

		try {
			restTemplate.postForObject(uri, entity, Object.class);
			log.debug("Sent SMS to '{}'", phoneNumber);
			// } catch (URISyntaxException e) {
			// log.error("URISyntaxException > URI " + e.getMessage());
		} catch (HttpClientErrorException e) {
			log.error("HttpClientErrorException > HTTP status code : " + e.getStatusCode().value());
			log.error("HttpClientErrorException > responseBodyAsString | " + e.getResponseBodyAsString());
			log.error("HttpClientErrorException > Message : " + e.getMessage());
			if (e.getStatusCode().value() == 403) {

			}
			if (e.getStatusCode().value() == 401) {
				// can cause a loop infinite if auth is not correct
				// this.akeneoOauthService.authenticate();
				// return getAllProduc
			}
		} catch (HttpServerErrorException e) {
			log.error("HttpServerErrorException > HTTP status code | " + e.getStatusCode().value());
			log.error("HttpServerErrorException > Message | " + e.getMessage());
			log.error("HttpServerErrorException > responseBodyAsString | " + e.getResponseBodyAsString());
		} catch (RestClientException e) {
			log.error("RestClientException > HTTP status code | " + e.getMessage());
			if (e instanceof HttpStatusCodeException) {
				String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
				log.error("RestClientException > HttpStatusCodeException > responseBodyAsString | " + errorResponse);
			}
		} catch (Exception e) {
			// Remonter une erreur
			log.debug("An error occured when trying to send SMS to '{}'", phoneNumber);
			e.printStackTrace();
		}
	}
}
