package com.kaylene.wifi.service.orangeapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.kaylene.wifi.domain.Record;

@Service
public class OrangeSmsApiService {

	private final static Logger log = LoggerFactory.getLogger(OrangeSmsApiService.class);

    
    // move to config
    public final static String SMS_SENDER_NAME = "KAYLENE-Wifi";
	public final static String GET_TOKEN_URL = "https://api.orange.com/oauth/v2/token";
	public final static String GET_TOKEN_AUTHORIZATION_HEADER = "Basic cDdXcGpreDQ2QmRUa2R0WkFaRkFXdGgxMDFiak9MRmk6SDd3VkdNSEVDdnNTVUJmUA==";
	public final static String GET_TOKEN_REQUEST_BODY = "grant_type=client_credentials";
	public final static String SEND_SMS_URL = "https://api.orange.com/smsmessaging/v1/outbound/tel%3A%2B221776149662/requests";
	public final static String SMS_SENDER_ADDRESS = "tel:+221782913636";
	
	private static final String TOKEN_FILE = "token_file.tmp";

	private final RestTemplate restTemplate;

	public OrangeSmsApiService(RestTemplateBuilder RestTemplateBuilder) {
		this.restTemplate = RestTemplateBuilder.build();
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

	public Optional<String> authenticate() {
		HttpHeaders headers = new HttpHeaders();
		// 'Content-Type': 'application/x-www-form-urlencoded
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// Basic Auth
		headers.add("Authorization", GET_TOKEN_AUTHORIZATION_HEADER);

		log.debug("Sending getToken request to Orange SMS API");

		HttpEntity<String> entity = new HttpEntity<String>(GET_TOKEN_REQUEST_BODY, headers);
		String token = null;
		try {
			Optional<Token> result = Optional
					.ofNullable(restTemplate.postForObject(GET_TOKEN_URL, entity, Token.class));
			if (result.isPresent()) {
				token = result.get().getAccess_token();
				log.debug("TOKEN : " + token);
				try (PrintWriter writer = new PrintWriter(TOKEN_FILE)) {
					writer.println(token);
				}
			}
		} catch (HttpClientErrorException e) {
			log.error("HttpClientErrorException > HTTP status code : " + e.getStatusCode().value());
			log.error("HttpClientErrorException > responseBodyAsString | " + e.getResponseBodyAsString());
			log.error("HttpClientErrorException > Message : " + e.getMessage());
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
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException > " + e.getMessage());
		} catch (Exception e) {
			log.error("Failed to get access token from Orange SMS API");
		}
		return Optional.ofNullable(token);
	}

	/**
	 * Get Saved token
	 */
	public String getAccesToken() throws URISyntaxException {
		String token = null;
		File file = new File(TOKEN_FILE);
		if (file.exists() && !file.isDirectory() && file.length() != 0) {
			token = this.authenticateWithToken().replaceAll("\n", "");
			log.info("token file >" + token);
		} else {
			Optional<String> optToken = authenticate();
			if (optToken.isPresent()) {
				token = optToken.get();
				log.info("token authentification >" + token);
			} else {
				log.error("Akeneo token not retrieved " + token);
			}
		}
		return token;
	}

	/**
	 * token persisten in Tempory file
	 */
	public String authenticateWithToken() {
		String token = null;
		try {
			return readFile(TOKEN_FILE);
		} catch (IOException e) {
			log.error("IOException > " + e.getMessage());
		}
		return token;
	}

	/**
	 * Read saved token from file
	 */
	public String readFile(String filename) throws IOException {
		String content = null;
		File file = new File(filename);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			log.error("IOException > " + e.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return content;
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
			outboundSMSMessageRequest.put("senderAddress", SMS_SENDER_ADDRESS);
			outboundSMSMessageRequest.put("senderName", SMS_SENDER_NAME);
			outboundSMSMessageRequest.put("outboundSMSTextMessage", outboundSMSTextMessage);
			requestBody.put("outboundSMSMessageRequest", outboundSMSMessageRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String token = null;
		try {
			token = getAccesToken();
		} catch (URISyntaxException e) {
			log.error("URISyntaxException " + e.getMessage());
			log.error(token);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer" + " " + token);

		HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);

		try {
			restTemplate.postForObject(new URI(SEND_SMS_URL), entity, Object.class);
			log.debug("Sent SMS to '{}'", phoneNumber);
			// } catch (URISyntaxException e) {
			// log.error("URISyntaxException > URI " + e.getMessage());
		} catch (HttpClientErrorException e) {
			log.error("HttpClientErrorException > HTTP status code : " + e.getStatusCode().value());
			log.error("HttpClientErrorException > responseBodyAsString | " + e.getResponseBodyAsString());
			log.error("HttpClientErrorException > Message : " + e.getMessage());
			// https://developer.orange.com/tech_guide/orange-apis-error-handling/
			if (e.getStatusCode().value() == 401) {
				authenticate();
				sendSms(phoneNumber, message);
			}
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
