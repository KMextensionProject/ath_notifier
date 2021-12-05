package com.crypto.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.util.StreamUtils;

import com.google.gson.Gson;

public class CryptoRequest {

	public static final String CURRENT_BITCOIN_PRICE_URL = "https://api.coinbase.com/v2/prices/spot?currency=";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCurrentBitcoinPrice(String currency) throws UnsupportedOperationException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(CURRENT_BITCOIN_PRICE_URL.concat(currency));
		request.addHeader(HttpHeaders.ACCEPT, "application/json");

		HttpResponse response = client.execute(request);
		HttpEntity body = response.getEntity();
		String jsonBody = StreamUtils.copyToString(body.getContent(), StandardCharsets.UTF_8);

		Gson gson = new Gson();
		return gson.fromJson(jsonBody, Map.class);
	}
}
