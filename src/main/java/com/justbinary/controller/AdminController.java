package com.justbinary.controller;

import com.justbinary.dto.response.ApiResponse;
import com.justbinary.model.User;
import com.justbinary.model.Wallet;
import com.justbinary.model.WithdrawalRecord;
import com.justbinary.model.WithdrawalRecord.Status;
import com.justbinary.repository.UserRepository;
import com.justbinary.repository.WalletRepository;
import com.justbinary.repository.WithdrawalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository       userRepository;
    private final WalletRepository     walletRepository;
    private final WithdrawalRepository withdrawalRepository;

    public AdminController(UserRepository userRepository,
                           WalletRepository walletRepository,
                           WithdrawalRepository withdrawalRepository) {
        this.userRepository       = userRepository;
        this.walletRepository     = walletRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    // ── 1. GET ALL CLIENTS ──────────────────────────────────────
    @GetMapping("/clients")
    public ResponseEntity<?> getAllClients() {
        try {
            List<User> users = userRepository.findAll();
            List<Map<String, Object>> clientList = new ArrayList<>();
            for (User user : users) {
                Optional<Wallet> walletOpt = walletRepository.findByUserId(user.getId());
                Double realBalance = walletOpt.isPresent() ? walletOpt.get().getRealBalance() : 0.0;
                Double demoBalance = walletOpt.isPresent() ? walletOpt.get().getDemoBalance() : 10000.0;
                Map<String, Object> map = new HashMap<>();
                map.put("id",          user.getId());
                map.put("name",        user.getFullName());
                map.put("email",       user.getEmail());
                map.put("phone",       user.getPhone() != null ? user.getPhone() : "");
                map.put("accountType", user.getAccountType());
                map.put("realBalance", realBalance);
                map.put("demoBalance", demoBalance);
                map.put("balance",     realBalance);
                map.put("status",      user.isEnabled() ? "active" : "inactive");
                clientList.add(map);
            }
            return ResponseEntity.ok(ApiResponse.success(clientList, "Clients fetched"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 2. ADD FUNDS TO REAL ACCOUNT ───────────────────────────
    @PostMapping("/clients/{id}/deposit")
    public ResponseEntity<?> addFunds(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Client not found", "NOT_FOUND"));

            Optional<Wallet> walletOpt = walletRepository.findByUserId(id);
            if (!walletOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Wallet not found", "NOT_FOUND"));

            Double amount = Double.parseDouble(body.get("amount").toString());
            if (amount <= 0)
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Amount must be greater than zero", "INVALID_AMOUNT"));

            User user     = userOpt.get();
            Wallet wallet = walletOpt.get();

            wallet.setRealBalance(wallet.getRealBalance() + amount);
            wallet.setTotalDeposited(wallet.getTotalDeposited() + amount);
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            Map<String, Object> result = new HashMap<>();
            result.put("newRealBalance", wallet.getRealBalance());
            result.put("clientName",     user.getFullName());

            return ResponseEntity.ok(ApiResponse.success(result,
                "Added $" + amount + " to " + user.getFullName() + "'s REAL account"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 3. DEDUCT FUNDS FROM REAL ACCOUNT ──────────────────────
    @PostMapping("/clients/{id}/deduct")
    public ResponseEntity<?> deductFunds(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Client not found", "NOT_FOUND"));

            Optional<Wallet> walletOpt = walletRepository.findByUserId(id);
            if (!walletOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Wallet not found", "NOT_FOUND"));

            Double amount = Double.parseDouble(body.get("amount").toString());
            if (amount <= 0)
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Amount must be greater than zero", "INVALID_AMOUNT"));

            User user     = userOpt.get();
            Wallet wallet = walletOpt.get();

            if (wallet.getRealBalance() < amount)
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Insufficient real balance", "INSUFFICIENT_BALANCE"));

            wallet.setRealBalance(wallet.getRealBalance() - amount);
            wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + amount);
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            Map<String, Object> result = new HashMap<>();
            result.put("newRealBalance", wallet.getRealBalance());
            result.put("clientName",     user.getFullName());

            return ResponseEntity.ok(ApiResponse.success(result,
                "Deducted $" + amount + " from " + user.getFullName() + "'s REAL account"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 4. SET ACCOUNT TYPE (REAL or DEMO) ─────────────────────
    @PostMapping("/clients/{id}/account-type")
    public ResponseEntity<?> setAccountType(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Client not found", "NOT_FOUND"));

            String type = body.get("accountType").toString().toUpperCase();
            if (!type.equals("REAL") && !type.equals("DEMO"))
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("accountType must be REAL or DEMO", "INVALID"));

            User user = userOpt.get();
            user.setAccountType(type);
            userRepository.save(user);

            Map<String, Object> result = new HashMap<>();
            result.put("userId",      user.getId());
            result.put("accountType", user.getAccountType());
            result.put("name",        user.getFullName());

            return ResponseEntity.ok(ApiResponse.success(result,
                "Account type set to " + type + " for " + user.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 5. DELETE CLIENT ───────────────────────────────────────
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<?> removeClient(@PathVariable String id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Client not found", "NOT_FOUND"));

            User user = userOpt.get();
            withdrawalRepository.deleteAll(withdrawalRepository.findByUserId(id));
            walletRepository.findByUserId(id).ifPresent(walletRepository::delete);
            userRepository.delete(user);

            return ResponseEntity.ok(ApiResponse.success(null,
                "Client " + user.getFullName() + " removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 6. GET ALL WITHDRAWALS ─────────────────────────────────
    @GetMapping("/withdrawals")
    public ResponseEntity<?> getAllWithdrawals() {
        try {
            List<WithdrawalRecord> records =
                withdrawalRepository.findAllByOrderByRequestedAtDesc();
            return ResponseEntity.ok(
                ApiResponse.success(buildWithdrawalList(records), "Withdrawals fetched"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 7. GET PENDING WITHDRAWALS ─────────────────────────────
    @GetMapping("/withdrawals/pending")
    public ResponseEntity<?> getPendingWithdrawals() {
        try {
            List<WithdrawalRecord> records =
                withdrawalRepository.findByStatus(Status.PENDING);
            return ResponseEntity.ok(
                ApiResponse.success(buildWithdrawalList(records), "Pending withdrawals fetched"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 8. APPROVE WITHDRAWAL ──────────────────────────────────
    @PostMapping("/withdrawals/{withdrawalId}/approve")
    public ResponseEntity<?> approveWithdrawal(@PathVariable String withdrawalId) {
        try {
            Optional<WithdrawalRecord> wrOpt =
                withdrawalRepository.findById(withdrawalId);
            if (!wrOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Withdrawal not found", "NOT_FOUND"));

            WithdrawalRecord wr = wrOpt.get();
            if (wr.getStatus() != Status.PENDING)
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Withdrawal already resolved", "ALREADY_RESOLVED"));

            Optional<Wallet> walletOpt =
                walletRepository.findByUserId(wr.getUserId());
            if (!walletOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Client wallet not found", "NOT_FOUND"));

            Wallet wallet = walletOpt.get();
            if (wallet.getRealBalance() < wr.getAmount())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Insufficient real balance", "INSUFFICIENT_BALANCE"));

            wallet.setRealBalance(wallet.getRealBalance() - wr.getAmount());
            wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + wr.getAmount());
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            wr.setStatus(Status.APPROVED);
            wr.setResolvedAt(LocalDateTime.now());
            withdrawalRepository.save(wr);

            Map<String, Object> result = new HashMap<>();
            result.put("withdrawalId",   wr.getId());
            result.put("amount",         wr.getAmount());
            result.put("newRealBalance", wallet.getRealBalance());

            return ResponseEntity.ok(
                ApiResponse.success(result, "Withdrawal approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 9. DENY WITHDRAWAL ─────────────────────────────────────
    @PostMapping("/withdrawals/{withdrawalId}/deny")
    public ResponseEntity<?> denyWithdrawal(@PathVariable String withdrawalId) {
        try {
            Optional<WithdrawalRecord> wrOpt =
                withdrawalRepository.findById(withdrawalId);
            if (!wrOpt.isPresent())
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Withdrawal not found", "NOT_FOUND"));

            WithdrawalRecord wr = wrOpt.get();
            if (wr.getStatus() != Status.PENDING)
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("Withdrawal already resolved", "ALREADY_RESOLVED"));

            wr.setStatus(Status.DENIED);
            wr.setResolvedAt(LocalDateTime.now());
            withdrawalRepository.save(wr);

            return ResponseEntity.ok(
                ApiResponse.success(null, "Withdrawal denied successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 10. DASHBOARD STATS ────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            List<User> allUsers = userRepository.findAll();
            long total = allUsers.size();
            long inactive = 0;
            double totalRealBalance = 0.0;

            for (User user : allUsers) {
                if (!user.isEnabled()) inactive++;
                Optional<Wallet> walletOpt =
                    walletRepository.findByUserId(user.getId());
                if (walletOpt.isPresent())
                    totalRealBalance += walletOpt.get().getRealBalance();
            }

            long pendingCount =
                withdrawalRepository.findByStatus(Status.PENDING).size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalClients",       total);
            stats.put("inactiveClients",    inactive);
            stats.put("totalRealBalance",   totalRealBalance);
            stats.put("pendingWithdrawals", pendingCount);

            return ResponseEntity.ok(ApiResponse.success(stats, "Stats fetched"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── 11. GET ALL TRADES ─────────────────────────────────────
    @GetMapping("/trades")
    public ResponseEntity<?> getAllTrades() {
        try {
            return ResponseEntity.ok(
                ApiResponse.success(new ArrayList<>(), "Trades fetched"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage(), "ERROR"));
        }
    }

    // ── HELPER ────────────────────────────────────────────────
    private List<Map<String, Object>> buildWithdrawalList(
            List<WithdrawalRecord> records) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (WithdrawalRecord wr : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("id",             wr.getId());
            map.put("clientId",       wr.getUserId());
            map.put("amount",         wr.getAmount());
            map.put("phoneNumber",    wr.getPhoneNumber());
            map.put("method",         wr.getMethod());
            map.put("accountDetails", wr.getAccountDetails());
            map.put("status",         wr.getStatus().name());
            map.put("requestedAt",    wr.getRequestedAt().toString());
            if (wr.getResolvedAt() != null)
                map.put("resolvedAt", wr.getResolvedAt().toString());
            result.add(map);
        }
        return result;
    }
}