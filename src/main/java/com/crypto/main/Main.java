package com.crypto.main;

import static com.crypto.enums.Currency.USD;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crypto.ath.BitcoinATHStore;
import com.crypto.http.CryptoRequest;
import com.crypto.messaging.MessageSender;
import com.twilio.type.PhoneNumber;

public class Main {

	private static final Logger logger = Logger.getAnonymousLogger();
	private static BitcoinATHStore athStore = BitcoinATHStore.getInstance();

	public static void main(String[] args) throws Exception {
		Logo.printLogo();

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> notifyATHWhenPresent(), 1, 5, TimeUnit.MINUTES);
	}

	private static void notifyATHWhenPresent() {
		try {
			double price = getCurrentPrice();
			if (athStore.isAboveATH(price)) {
				// to prevent sending notifications on multiple occasions in the same day
				if (hasNotNotifiedYet()) {
					sendNotification(price);
				}
				athStore.updateATH(price);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Http connection could have been aborted", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static double getCurrentPrice() throws IOException {
		Map<String, Object> bitcoinRequest = CryptoRequest.getCurrentBitcoinPrice(USD);
		Map<String, Object> bitcoinData = (Map<String, Object>) bitcoinRequest.get("data");
		return Double.valueOf(String.valueOf(bitcoinData.get("amount"))).doubleValue();
	}

	private static boolean hasNotNotifiedYet() {
		LocalDate currentDate = LocalDate.now();
		LocalDate lastUpdated = athStore.getLastUpdate();
		return !(currentDate.equals(lastUpdated));
	}

	private static void sendNotification(double price) {
		List<PhoneNumber> recipients = getLocalRecipientList();
		String message = athStore.getATHMessage(price, USD);
		MessageSender.sendSMS(recipients, message);
		logger.info(() -> "Notification has been sent to recipients: " + recipients);
	}

	private static List<PhoneNumber> getLocalRecipientList() {
		return Arrays.asList(
				new PhoneNumber("your_verified_number"));
	}
}
