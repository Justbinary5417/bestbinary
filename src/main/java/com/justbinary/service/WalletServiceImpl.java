package com.justbinary.service;

import com.justbinary.dto.DepositRequest;
import com.justbinary.dto.WithdrawRequest;
import com.justbinary.model.*;
import com.justbinary.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository      walletRepository;
    private final UserRepository        userRepository;
    private final WithdrawalRepository  withdrawalRepository;
    private final DepositRepository     depositRepository;
    private final MpesaService          mpesaService;

    public WalletServiceImpl(WalletRepository walletRepository,
                             UserRepository userRepository,
                             WithdrawalRepository withdrawalRepository,
                             DepositRepository depositRepository,
                             MpesaService mpesaService) {
        this.walletRepository     = walletRepository;
        this.userRepository       = userRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.depositRepository    = depositRepository;
        this.mpesaService         = mpesaService;
    }

    @Override
    public String getBalance(String email) {
        User user     = getUser(email);
        Wallet wallet = getWallet(user.getId());

        double realBalance    = wallet.getRealBalance()    != null ? wallet.getRealBalance()    : 0.0;
        double demoBalance    = wallet.getDemoBalance()    != null ? wallet.getDemoBalance()    : 0.0;
        double totalDeposited = wallet.getTotalDeposited() != null ? wallet.getTotalDeposited() : 0.0;
        double totalWithdrawn = wallet.getTotalWithdrawn() != null ? wallet.getTotalWithdrawn() : 0.0;

        return "{"
             + "\"realBalance\":"    + realBalance    + ","
             + "\"demoBalance\":"    + demoBalance    + ","
             + "\"accountType\":\"" + user.getAccountType() + "\","
             + "\"totalDeposited\":" + totalDeposited + ","
             + "\"totalWithdrawn\":" + totalWithdrawn
             + "}";
    }

    @Override
    public String deposit(String email, DepositRequest request) {
        User user = getUser(email);

        if ("DEMO".equalsIgnoreCase(user.getAccountType())) {
            throw new RuntimeException("Deposits are only allowed on REAL accounts.");
        }

        double amount = request.getAmount().doubleValue();
        if (amount < 10) {
            throw new RuntimeException("Minimum deposit is KES 10");
        }

        String checkoutRequestId = mpesaService.stkPush(
            request.getPhoneNumber(), amount, "JustBinary Deposit"
        );

        DepositRecord deposit = new DepositRecord();
        deposit.setUserId(user.getId());
        deposit.setAmount(amount);
        deposit.setPhoneNumber(request.getPhoneNumber());
        deposit.setMpesaCheckoutRequestId(checkoutRequestId);
        deposit.setStatus(DepositRecord.Status.PENDING);
        deposit.setRequestedAt(LocalDateTime.now());
        depositRepository.save(deposit);

        return "M-Pesa prompt sent to " + request.getPhoneNumber()
             + ". Please enter your PIN to complete deposit of KES " + amount;
    }

    @Override
    public String withdraw(String email, WithdrawRequest request) {
        User user = getUser(email);

        if ("DEMO".equalsIgnoreCase(user.getAccountType())) {
            throw new RuntimeException("Withdrawals only allowed on REAL accounts.");
        }

        String method = request.getMethod();
        if (method == null || method.isBlank()) {
            throw new RuntimeException("Please select a withdrawal method.");
        }

        if (request.getAmount() == null) {
            throw new RuntimeException("Withdrawal amount is required.");
        }

        double amount = request.getAmount().doubleValue();

        if (amount < 1) {
            throw new RuntimeException("Minimum withdrawal is $1");
        }

        Wallet wallet      = getWallet(user.getId());
        double realBalance = wallet.getRealBalance() != null ? wallet.getRealBalance() : 0.0;

        if (realBalance < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        if ("MPESA".equalsIgnoreCase(method)) {
            if (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
                throw new RuntimeException("Phone number is required for M-Pesa withdrawal.");
            }
        } else {
            if (request.getAccountDetails() == null || request.getAccountDetails().isBlank()) {
                throw new RuntimeException("Account details are required for " + method + " withdrawal.");
            }
        }

        WithdrawalRecord record = new WithdrawalRecord();
        record.setUserId(user.getId());
        record.setAmount(amount);
        record.setMethod(method);
        record.setPhoneNumber(request.getPhoneNumber());
        record.setAccountDetails(request.getAccountDetails());
        record.setStatus(WithdrawalRecord.Status.PENDING);
        record.setRequestedAt(LocalDateTime.now());
        withdrawalRepository.save(record);

        return "Withdrawal of $" + amount + " via " + method
             + " submitted. Awaiting admin approval.";
    }

    @Override
    public String getTransactionHistory(String email) {
        User user = getUser(email);
        List<WithdrawalRecord> withdrawals = withdrawalRepository.findByUserId(user.getId());
        List<DepositRecord>    deposits    = depositRepository.findByUserId(user.getId());

        StringBuilder json = new StringBuilder();
        json.append("{\"content\":[");

        boolean first = true;

        for (WithdrawalRecord w : withdrawals) {
            if (!first) json.append(",");
            json.append("{")
                .append("\"type\":\"WITHDRAWAL\",")
                .append("\"amount\":").append(w.getAmount()).append(",")
                .append("\"status\":\"").append(w.getStatus()).append("\",")
                .append("\"description\":\"Withdrawal via ").append(w.getMethod()).append("\",")
                .append("\"createdAt\":\"").append(w.getRequestedAt()).append("\"")
                .append("}");
            first = false;
        }

        for (DepositRecord d : deposits) {
            if (!first) json.append(",");
            json.append("{")
                .append("\"type\":\"DEPOSIT\",")
                .append("\"amount\":").append(d.getAmount()).append(",")
                .append("\"status\":\"").append(d.getStatus()).append("\",")
                .append("\"description\":\"Deposit via M-Pesa\",")
                .append("\"createdAt\":\"").append(d.getRequestedAt()).append("\"")
                .append("}");
            first = false;
        }

        json.append("]}");
        return json.toString();
    }

    @Override
    public String approveWithdrawal(String withdrawalId) {
        WithdrawalRecord record = withdrawalRepository.findById(withdrawalId)
            .orElseThrow(() -> new RuntimeException("Withdrawal not found"));

        if (record.getStatus() == WithdrawalRecord.Status.APPROVED) {
            throw new RuntimeException("Withdrawal already approved");
        }

        Wallet wallet = getWallet(record.getUserId());
        double currentBalance = wallet.getRealBalance() != null ? wallet.getRealBalance() : 0.0;

        if (currentBalance < record.getAmount()) {
            throw new RuntimeException("Insufficient balance to approve this withdrawal");
        }

        wallet.setRealBalance(currentBalance - record.getAmount());
        wallet.setTotalWithdrawn(
            (wallet.getTotalWithdrawn() != null ? wallet.getTotalWithdrawn() : 0.0)
            + record.getAmount()
        );
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        record.setStatus(WithdrawalRecord.Status.APPROVED);
        withdrawalRepository.save(record);

        return "Withdrawal of $" + record.getAmount() + " approved successfully";
    }

    @Override
    public void savePendingDeposit(String phoneNumber, double usdAmount,
                                    int kesAmount, String checkoutRequestId) {
        // ✅ FIXED: Try 254XXXXXXXXX first, then fallback to 07XXXXXXXXX
        String localFormat = phoneNumber.startsWith("254")
                ? "0" + phoneNumber.substring(3)
                : phoneNumber;

        User user = userRepository.findByPhone(phoneNumber)
                .or(() -> userRepository.findByPhone(localFormat))
                .orElseThrow(() -> new RuntimeException(
                        "User not found for phone: " + phoneNumber));

        DepositRecord deposit = new DepositRecord();
        deposit.setUserId(user.getId());
        deposit.setAmount(usdAmount);
        deposit.setPhoneNumber(phoneNumber);
        deposit.setMpesaCheckoutRequestId(checkoutRequestId);
        deposit.setStatus(DepositRecord.Status.PENDING);
        deposit.setRequestedAt(LocalDateTime.now());
        depositRepository.save(deposit);
    }

    @Override
    public void confirmDeposit(String checkoutRequestId, double usdAmount,
                                String mpesaReceipt, String phoneNumber) {
        DepositRecord deposit = depositRepository
                .findByMpesaCheckoutRequestId(checkoutRequestId)
                .orElseThrow(() -> new RuntimeException("Deposit not found"));

        deposit.setStatus(DepositRecord.Status.COMPLETED);
        deposit.setAmount(usdAmount);
        depositRepository.save(deposit);

        Wallet wallet = getWallet(deposit.getUserId());
        wallet.setRealBalance(
                (wallet.getRealBalance() != null ? wallet.getRealBalance() : 0.0)
                + usdAmount);
        wallet.setTotalDeposited(
                (wallet.getTotalDeposited() != null ? wallet.getTotalDeposited() : 0.0)
                + usdAmount);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    @Override
    public void failDeposit(String checkoutRequestId) {
        depositRepository
                .findByMpesaCheckoutRequestId(checkoutRequestId)
                .ifPresent(deposit -> {
                    deposit.setStatus(DepositRecord.Status.FAILED);
                    depositRepository.save(deposit);
                });
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Wallet getWallet(String userId) {
        return walletRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}