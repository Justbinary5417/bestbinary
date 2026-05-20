package com.justbinary.service;

import com.justbinary.config.MpesaConfig;
import com.justbinary.dto.response.WithdrawResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MpesaService {

    private final MpesaConfig mpesaConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, WithdrawResponse> transactionStore = new ConcurrentHashMap<>();

    public MpesaService(MpesaConfig mpesaConfig) {
        this.mpesaConfig = mpesaConfig;
    }

    public String getAccessToken() {
        String credentials = mpesaConfig.getConsumerKey() + ":" + mpesaConfig.getConsumerSecret();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            mpesaConfig.getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials",
            HttpMethod.GET, entity, Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public String stkPush(String phone, double amount, String checkoutDesc) {
        String token = getAccessToken();
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String rawPassword = mpesaConfig.getShortcode()
            + mpesaConfig.getPasskey() + timestamp;
        String password = Base64.getEncoder()
            .encodeToString(rawPassword.getBytes());

        if (phone.startsWith("0")) {
            phone = "254" + phone.substring(1);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("BusinessShortCode", mpesaConfig.getShortcode());
        body.put("Password", password);
        body.put("Timestamp", timestamp);
        body.put("TransactionType", "CustomerPayBillOnline");
        body.put("Amount", (int) amount);
        body.put("PartyA", phone);
        body.put("PartyB", mpesaConfig.getShortcode());
        body.put("PhoneNumber", phone);
        body.put("CallBackURL", mpesaConfig.getCallbackUrl());
        body.put("AccountReference", "JustBinary");
        body.put("TransactionDesc", checkoutDesc);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            mpesaConfig.getBaseUrl() + "/mpesa/stkpush/v1/processrequest",
            entity, Map.class
        );

        return (String) response.getBody().get("CheckoutRequestID");
    }

    public WithdrawResponse initiateWithdrawal(String phoneNumber, BigDecimal amount) {
        try {
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "254" + phoneNumber.substring(1);
            }

            String checkoutRequestId = stkPush(phoneNumber, amount.doubleValue(), "Withdrawal");

            WithdrawResponse response = WithdrawResponse.success(
                checkoutRequestId,
                amount,
                null,
                null,
                phoneNumber
            );

            transactionStore.put(checkoutRequestId, response);

            return response;

        } catch (Exception e) {
            return WithdrawResponse.failure("Withdrawal failed: " + e.getMessage());
        }
    }

    public boolean verifyTransaction(String transactionId) {
        WithdrawResponse response = transactionStore.get(transactionId);
        return response != null && response.isSuccess();
    }
}