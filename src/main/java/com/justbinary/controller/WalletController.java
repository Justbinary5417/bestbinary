package com.justbinary.controller;

import com.justbinary.dto.DepositRequest;
import com.justbinary.dto.WithdrawRequest;
import com.justbinary.model.Wallet;
import com.justbinary.model.User;
import com.justbinary.repository.UserRepository;
import com.justbinary.repository.WalletRepository;
import com.justbinary.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    private final WalletService    walletService;
    private final WalletRepository walletRepository;
    private final UserRepository   userRepository;

    public WalletController(WalletService walletService,
                            WalletRepository walletRepository,
                            UserRepository userRepository) {
        this.walletService    = walletService;
        this.walletRepository = walletRepository;
        this.userRepository   = userRepository;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication) {
        try {
            String email = authentication.getName();

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (!userOpt.isPresent())
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));

            Optional<Wallet> walletOpt = walletRepository.findByUserId(userOpt.get().getId());
            if (!walletOpt.isPresent())
                return ResponseEntity.status(404).body(Map.of("error", "Wallet not found"));

            Wallet wallet = walletOpt.get();

            Map<String, Object> result = new HashMap<>();
            result.put("realBalance",    wallet.getRealBalance()    != null ? wallet.getRealBalance()    : 0.0);
            result.put("demoBalance",    wallet.getDemoBalance()    != null ? wallet.getDemoBalance()    : 0.0);
            result.put("totalDeposited", wallet.getTotalDeposited() != null ? wallet.getTotalDeposited() : 0.0);
            result.put("totalWithdrawn", wallet.getTotalWithdrawn() != null ? wallet.getTotalWithdrawn() : 0.0);
            result.put("updatedAt",      wallet.getUpdatedAt() != null ? wallet.getUpdatedAt().toString() : "");

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest request,
                                     Authentication authentication) {
        try {
            String email  = authentication.getName();
            String result = walletService.deposit(email, request);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request,
                                      Authentication authentication) {
        try {
            String email  = authentication.getName();
            String result = walletService.withdraw(email, request);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ FIXED — returns proper JSON content for frontend to show PENDING/APPROVED
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "ALL") String type,
            Authentication authentication) {
        try {
            String email  = authentication.getName();
            String result = walletService.getTransactionHistory(email);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ ADDED — admin approves withdrawal, status changes PENDING → APPROVED
    @PostMapping("/withdraw/approve/{withdrawalId}")
    public ResponseEntity<?> approveWithdrawal(
            @PathVariable String withdrawalId) {
        try {
            String result = walletService.approveWithdrawal(withdrawalId);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/demo/reset")
    public ResponseEntity<?> resetDemo(Authentication authentication) {
        try {
            String email = authentication.getName();

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (!userOpt.isPresent())
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));

            Optional<Wallet> walletOpt = walletRepository.findByUserId(userOpt.get().getId());
            if (!walletOpt.isPresent())
                return ResponseEntity.status(404).body(Map.of("error", "Wallet not found"));

            Wallet wallet = walletOpt.get();
            wallet.setDemoBalance(10000.0);
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            return ResponseEntity.ok(Map.of(
                "demoBalance", 10000.0,
                "realBalance", wallet.getRealBalance() != null ? wallet.getRealBalance() : 0.0,
                "message",     "Demo balance reset to $10,000"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}