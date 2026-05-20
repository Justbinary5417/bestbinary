package com.justbinary.service;

import com.justbinary.config.DarajaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class DarajaService {

    @Autowired
    private DarajaConfig darajaConfig;

    @Autowired
    private RestTemplate restTemplate;

    public String getAccessToken() {
        String credentials = darajaConfig.getConsumerKey()
                + ":" + darajaConfig.getConsumerSecret();
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = darajaConfig.getBaseUrl()
                + "/oauth/v1/generate?grant_type=client_credentials";

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null)
            return (String) response.getBody().get("access_token");

        throw new RuntimeException("Failed to get access token");
    }

    private String generatePassword(String timestamp) {
        String raw = darajaConfig.getTillNumber()  // ✅ Fixed: use tillNumber
                + darajaConfig.getPasskey()
                + timestamp;
        return Base64.getEncoder()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, Object> initiateSTKPush(String phoneNumber,
                                                int kesAmount,
                                                double usdAmount,
                                                String accountRef) {
        String accessToken = getAccessToken();
        String timestamp   = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password    = generatePassword(timestamp);

        Map<String, Object> body = new HashMap<>();
        body.put("BusinessShortCode", darajaConfig.getTillNumber());  // ✅ Fixed
        body.put("Password",          password);
        body.put("Timestamp",         timestamp);
        body.put("TransactionType",   "CustomerBuyGoodsOnline");      // ✅ Fixed
        body.put("Amount",            kesAmount);
        body.put("PartyA",            phoneNumber);
        body.put("PartyB",            darajaConfig.getTillNumber());  // ✅ Correct
        body.put("PhoneNumber",       phoneNumber);
        body.put("CallBackURL",       darajaConfig.getCallbackUrl());
        body.put("AccountReference",  accountRef);
        body.put("TransactionDesc",   "JustBinary Deposit $" + usdAmount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = darajaConfig.getBaseUrl()
                + "/mpesa/stkpush/v1/processrequest";

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class);

        if (response.getBody() != null) return response.getBody();
        throw new RuntimeException("STK Push failed");
    }

    public Map<String, Object> querySTKStatus(String checkoutRequestId) {
        String accessToken = getAccessToken();
        String timestamp   = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password    = generatePassword(timestamp);

        Map<String, Object> body = new HashMap<>();
        body.put("BusinessShortCode",  darajaConfig.getTillNumber()); // ✅ Fixed
        body.put("Password",           password);
        body.put("Timestamp",          timestamp);
        body.put("CheckoutRequestID",  checkoutRequestId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = darajaConfig.getBaseUrl()
                + "/mpesa/stkpushquery/v1/query";

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class);

        if (response.getBody() != null) return response.getBody();
        throw new RuntimeException("STK Query failed");
    }
}