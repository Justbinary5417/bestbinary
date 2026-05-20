package com.justbinary.service;

import com.justbinary.config.PesapalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PesapalService {

    @Autowired
    private PesapalConfig pesapalConfig;

    @Autowired
    private RestTemplate restTemplate;

    // ── STEP 1: GET AUTH TOKEN ──────────────────────
    public String getToken() {
        String url = pesapalConfig.getBaseUrl() + "/api/Auth/RequestToken";

        Map<String, String> body = new HashMap<>();
        body.put("consumer_key", pesapalConfig.getConsumerKey());
        body.put("consumer_secret", pesapalConfig.getConsumerSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        System.out.println("TOKEN RESPONSE: " + response.getBody());
        return (String) response.getBody().get("token");
    }

    // ── STEP 2: REGISTER IPN ────────────────────────
    public String registerIPN(String token) {
        String url = pesapalConfig.getBaseUrl() + "/api/URLSetup/RegisterIPN";

        Map<String, String> body = new HashMap<>();
        body.put("url", pesapalConfig.getIpnUrl());
        body.put("ipn_notification_type", "GET");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        System.out.println("IPN RESPONSE: " + response.getBody());
        return (String) response.getBody().get("ipn_id");
    }

    // ── STEP 3: SUBMIT ORDER (triggers STK push) ────
    public Map<String, Object> submitOrder(String phone, double amount,
                                            String currency, String description) {
        String token = getToken();

        // Try to register IPN — continue even if it fails
        String ipnId = null;
        try {
            ipnId = registerIPN(token);
        } catch (Exception e) {
            System.out.println("IPN REGISTRATION FAILED: " + e.getMessage());
        }

        String url = pesapalConfig.getBaseUrl() + "/api/Transactions/SubmitOrderRequest";
        String reference = "JB-" + System.currentTimeMillis();

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("phone_number", phone);
        billingAddress.put("email_address", "customer@justbinary.com");

        Map<String, Object> body = new HashMap<>();
        body.put("id", reference);
        body.put("currency", currency);
        body.put("amount", amount);
        body.put("description", description);
        body.put("callback_url", pesapalConfig.getCallbackUrl());
        body.put("billing_address", billingAddress);

        // Only add notification_id if we have one
        if (ipnId != null && !ipnId.isEmpty()) {
            body.put("notification_id", ipnId);
        }

        System.out.println("SUBMITTING ORDER: " + body);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        System.out.println("ORDER RESPONSE: " + response.getBody());
        return response.getBody();
    }

    // ── STEP 4: CHECK PAYMENT STATUS ───────────────
    public Map<String, Object> checkStatus(String orderTrackingId) {
        String token = getToken();

        String url = pesapalConfig.getBaseUrl()
                + "/api/Transactions/GetTransactionStatus?orderTrackingId="
                + orderTrackingId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, request, Map.class);

        System.out.println("STATUS RESPONSE: " + response.getBody());
        return response.getBody();
    }
}