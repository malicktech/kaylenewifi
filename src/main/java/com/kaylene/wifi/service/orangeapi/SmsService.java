package com.kaylene.wifi.service.orangeapi;

import java.text.Normalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kaylene.wifi.domain.Record;

@Service
public class SmsService {
	
	private final Logger log = LoggerFactory.getLogger(SmsService.class);
	
	@Autowired
	OrangeSmsApiService orangeSmsApiService;
	
	@Async
	public void sendCodeSms(Record record) {
		// TODO verif to delete
		String phoneNumber = record.getPhone();
		if (phoneNumber == null) {
			log.error("Aucun numéro de téléphone fournis : '{}'", record);
		} else {
			String message = createCodeSmsMessage(record);
			orangeSmsApiService.sendSms(phoneNumber, message);
		}
	}
	
	private String createCodeSmsMessage(Record record) {
		String message = "Event: " + record.getEvent().getName() + "\n" +
				"Code WIFI: "+ record.getCode() + "\n"+
				"\n"+
				"Merci pour votre Achat";
		//Delete accents
		message = Normalizer.normalize(message, Normalizer.Form.NFD);
		message = message.replaceAll("[^\\p{ASCII}]", "");
		return message;
	}

}
