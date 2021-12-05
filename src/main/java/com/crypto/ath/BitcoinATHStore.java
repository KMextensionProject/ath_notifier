package com.crypto.ath;

import static com.crypto.enums.Resources.BITCOIN_ATH_STORE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

public final class BitcoinATHStore implements ATHStore {

	private static Properties athStore;

	static {
		initATHStore();
	}
	
	private static final BitcoinATHStore INSTANCE = new BitcoinATHStore();

	private BitcoinATHStore() {	}

	public static BitcoinATHStore getInstance() {
		return INSTANCE;
	}

	@Override
	public double getCurrentATH() {
		return Double.valueOf(athStore.getProperty("ath_price")).doubleValue();
	}

	@Override
	public void updateATH(double price) throws IOException {
		athStore.setProperty("ath_price", String.valueOf(price));
		athStore.setProperty("last_updated", LocalDate.now().toString());
		athStore.store(new BufferedWriter(new FileWriter(BITCOIN_ATH_STORE)), null);
	}

	@Override
	public boolean isAboveATH(double price) {
		double currentAth = Double.valueOf(athStore.getProperty("ath_price")).doubleValue();
		return price > currentAth;
	}

	public LocalDate getLastUpdate() {
		return LocalDate.parse(athStore.getProperty("last_updated"));
	}

	/*
	 * The properties file should not be tempered after the project build.
	 * So in this case, it is untraditional, but we use an external properties
	 * file to store current ATH on the relative path to the build JAR file.
	 */
	private static void initATHStore() {
		athStore = new Properties();
		try {
//			InputStream iStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(BITCOIN_ATH_STORE);
			athStore.load(new BufferedReader(new FileReader(BITCOIN_ATH_STORE)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getATHMessage(double price, String currency) {
		return ">\nBitcoin reached new ATH with price " + price + " " + currency;
	}
}
