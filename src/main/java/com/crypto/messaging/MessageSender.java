package com.crypto.messaging;

import static com.crypto.enums.Resources.*;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class MessageSender {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static String SID;
	private static String TOKEN;
	private static PhoneNumber NUMBER;

	static {
		configure();
	}

	public static void sendSMS(PhoneNumber recipient, String message) {
		Message.creator(recipient, NUMBER, message).create();
	}

	public static void sendSMS(List<PhoneNumber> recipientList, String message) {
		recipientList.forEach(recipient -> sendSMS(recipient, message));
	}

	private static void configure() {
		Properties config = new Properties();
		try {
			config.load(MessageSender.class.getClassLoader().getResourceAsStream(TWILLIO_CONFIG));
			SID = config.getProperty("sid");
			TOKEN = config.getProperty("token");
			NUMBER = new PhoneNumber(config.getProperty("number"));

			Twilio.init(SID, TOKEN);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Unable to load twilio account configuration", ex);
		}
	}
}
