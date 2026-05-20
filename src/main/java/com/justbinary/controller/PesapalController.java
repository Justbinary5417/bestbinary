package com.justbinary.controller;

import com.justbinary.service.PesapalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pesapal")
@CrossOrigin(origins = "*")
public class PesapalController {

    @Autowired
    private PesapalService pesapalService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> payload) {
        try {
            String phone       = (String) payload.get("phone");
            double amount      = Double.parseDouble(payload.get("amount").toString());
            String currency    = payload.getOrDefault("currency", "KES").toString();
            String description = payload.getOrDefault("description", "JustBinary Deposit").toString();

            if (phone.startsWith("+")) phone = phone.substring(1);
            if (phone.startsWith("0")) phone = "254" + phone.substring(1);

            Map<String, Object> result = pesapalService.submitOrder(
                    phone, amount, currency, description);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionId", result.getOrDefault("order_tracking_id", ""),
                "redirectUrl",   result.getOrDefault("redirect_url", ""),
                "message",       "STK Push initiated successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/status/{orderTrackingId}")
    public ResponseEntity<?> checkStatus(@PathVariable String orderTrackingId) {
        try {
            Map<String, Object> result = pesapalService.checkStatus(orderTrackingId);
            String status = result.getOrDefault("payment_status_description", "PENDING").toString();

            String appStatus;
            switch (status.toUpperCase()) {
                case "COMPLETED": appStatus = "COMPLETED"; break;
                case "FAILED":    appStatus = "FAILED";    break;
                case "INVALID":   appStatus = "FAILED";    break;
                default:          appStatus = "PENDING";
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "status",  appStatus,
                "message", status
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "status",  "FAILED",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/ipn")
    public ResponseEntity<?> ipnCallback(
            @RequestParam String orderTrackingId,
            @RequestParam String orderMerchantReference) {
        try {
            Map<String, Object> result = pesapalService.checkStatus(orderTrackingId);
            String status = result.getOrDefault("payment_status_description", "PENDING").toString();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "status",  status,
                "orderTrackingId", orderTrackingId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<?> paymentCallback(
            @RequestParam String orderTrackingId,
            @RequestParam String orderMerchantReference) {
        try {
            Map<String, Object> result = pesapalService.checkStatus(orderTrackingId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        }
    }
}