package com.justbinary.controller;

import com.justbinary.config.DarajaConfig;
import com.justbinary.service.DarajaService;
import com.justbinary.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired private DarajaService darajaService;
    @Autowired private WalletService walletService;
    @Autowired private DarajaConfig  darajaConfig;

    // 1. INITIATE STK PUSH
    @PostMapping("/stk-push")
    public ResponseEntity<?> initiateStkPush(
            @RequestBody Map<String, Object> request) {
        try {
            String phoneNumber = (String) request.get("phoneNumber");
            double usdAmount   = Double.parseDouble(
                    request.get("usdAmount").toString());
            int kesAmount      = (int) Math.round(
                    usdAmount * darajaConfig.getUsdToKes());
            String accountRef  = request.getOrDefault(
                    "accountReference", "JustBinary").toString();

            if (usdAmount < 1.0)
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Minimum deposit is $1"));

            if (!phoneNumber.matches("^254[0-9]{9}$"))
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid phone number format"));

            Map<String, Object> stkResponse = darajaService.initiateSTKPush(
                    phoneNumber, kesAmount, usdAmount, accountRef);

            String responseCode = stkResponse
                    .getOrDefault("ResponseCode", "1").toString();

            if ("0".equals(responseCode)) {
                String checkoutRequestId = stkResponse
                        .get("CheckoutRequestID").toString();

                walletService.savePendingDeposit(
                        phoneNumber, usdAmount, kesAmount, checkoutRequestId);

                return ResponseEntity.ok(Map.of(
                        "success",           true,
                        "message",           "STK Push sent successfully",
                        "checkoutRequestId", checkoutRequestId,
                        "kesAmount",         kesAmount,
                        "usdAmount",         usdAmount
                ));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", stkResponse.getOrDefault(
                            "errorMessage", "STK Push failed")));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed: " + e.getMessage()));
        }
    }

    // 2. CHECK STK STATUS
    @GetMapping("/stk-status")
    public ResponseEntity<?> checkStkStatus(
            @RequestParam String checkoutRequestId) {
        try {
            Map<String, Object> result =
                    darajaService.querySTKStatus(checkoutRequestId);
            String resultCode = result
                    .getOrDefault("ResultCode", "-1").toString();

            if ("0".equals(resultCode))
                return ResponseEntity.ok(Map.of(
                        "status", "SUCCESS", "ResultCode", resultCode));

            if ("1032".equals(resultCode))
                return ResponseEntity.ok(Map.of(
                        "status", "FAILED", "ResultCode", resultCode));

            return ResponseEntity.ok(Map.of(
                    "status", "PENDING", "ResultCode", resultCode));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "PENDING", "message", "Checking..."));
        }
    }

    // 3. M-PESA CALLBACK (Safaricom calls this automatically)
    @PostMapping("/mpesa-callback")
    public ResponseEntity<?> mpesaCallback(
            @RequestBody Map<String, Object> callbackData) {
        try {
            Map<String, Object> body =
                    (Map<String, Object>) callbackData.get("Body");
            Map<String, Object> stkCallback =
                    (Map<String, Object>) body.get("stkCallback");

            String checkoutRequestId =
                    stkCallback.get("CheckoutRequestID").toString();
            int resultCode = Integer.parseInt(
                    stkCallback.get("ResultCode").toString());

            if (resultCode == 0) {
                Map<String, Object> metadata =
                        (Map<String, Object>) stkCallback.get("CallbackMetadata");
                List<Map<String, Object>> items =
                        (List<Map<String, Object>>) metadata.get("Item");

                double amount       = 0;
                String mpesaReceipt = "";
                String phoneNumber  = "";

                for (Map<String, Object> item : items) {
                    String name  = item.get("Name").toString();
                    Object value = item.get("Value");
                    if (value == null) continue;
                    switch (name) {
                        case "Amount":
                            amount = Double.parseDouble(value.toString());
                            break;
                        case "MpesaReceiptNumber":
                            mpesaReceipt = value.toString();
                            break;
                        case "PhoneNumber":
                            phoneNumber = value.toString();
                            break;
                    }
                }

                double usdAmount = amount / darajaConfig.getUsdToKes();
                walletService.confirmDeposit(
                        checkoutRequestId, usdAmount, mpesaReceipt, phoneNumber);
            } else {
                walletService.failDeposit(checkoutRequestId);
            }

            return ResponseEntity.ok(Map.of(
                    "ResultCode", 0, "ResultDesc", "Accepted"));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "ResultCode", 0, "ResultDesc", "Accepted"));
        }
    }
}