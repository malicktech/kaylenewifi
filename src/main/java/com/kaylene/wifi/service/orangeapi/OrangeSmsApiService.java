package com.kaylene.wifi.service.orangeapi;

import java.net.URI;
import java.text.Normalizer;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.kaylene.wifi.config.Constants;
import com.kaylene.wifi.domain.Record;

@Service
public class OrangeSmsApiService {

	private final static Logger log = LoggerFactory.getLogger(OrangeSmsApiService.class);

	private final RestTemplate restTemplate;

	public OrangeSmsApiService(RestTemplateBuilder RestTemplateBuilder) {
		this.restTemplate = RestTemplateBuilder. build();
	}

	@Async
	public void sendCodeSms(Record record) {
		String message = createCodeSmsMessage(record);
		sendSms(record.getPhone(), message);
	}

	private String createCodeSmsMessage(Record record) {
		String message = "Bonjour, votre code d’accès au WIFI-KAYLENE est le :" + record.getCode();
		// Normalize to delete accents
		return Normalizer.normalize(message, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public String getAccesToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", Constants.GET_TOKEN_AUTHORIZATION_HEADER);

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

		// SMS Max Length : 140 char
		if (message.length() > 140)
			message = message.substring(0, 140);

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
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer" + " " + getAccesToken());

		HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);

		try {
			restTemplate.postForObject(new URI(Constants.SEND_SMS_URL), entity, Object.class);
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
