package com.justbinary.controller;

import com.justbinary.model.DepositRecord;
import com.justbinary.model.Wallet;
import com.justbinary.repository.DepositRepository;
import com.justbinary.repository.WalletRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/mpesa")
@CrossOrigin(origins = "*")
public class MpesaCallbackController {

    private final DepositRepository depositRepository;
    private final WalletRepository walletRepository;

    public MpesaCallbackController(DepositRepository depositRepository,
                                   WalletRepository walletRepository) {
        this.depositRepository = depositRepository;
        this.walletRepository  = walletRepository;
    }

    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> body = (Map<String, Object>)
                payload.get("Body");
            Map<String, Object> stkCallback = (Map<String, Object>)
                body.get("stkCallback");

            String checkoutRequestId = 
                (String) stkCallback.get("CheckoutRequestID");
            int resultCode = 
                (int) stkCallback.get("ResultCode");

            DepositRecord deposit = depositRepository
                .findByMpesaCheckoutRequestId(checkoutRequestId)
                .orElse(null);

            if (deposit == null) return ResponseEntity.ok("ignored");

            if (resultCode == 0) {
                // Payment successful — update wallet balance
                Wallet wallet = walletRepository
                    .findByUserId(deposit.getUserId())
                    .orElseThrow();

                wallet.setBalance(wallet.getBalance() + deposit.getAmount());
                wallet.setTotalDeposited(
                    wallet.getTotalDeposited() + deposit.getAmount());
                wallet.setUpdatedAt(LocalDateTime.now());
                walletRepository.save(wallet);

                deposit.setStatus(DepositRecord.Status.COMPLETED);
                deposit.setCompletedAt(LocalDateTime.now());
            } else {
                deposit.setStatus(DepositRecord.Status.FAILED);
            }

            depositRepository.save(deposit);
            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            return ResponseEntity.ok("error: " + e.getMessage());
        }
    }
}