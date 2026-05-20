package com.justbinary.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawResponse {

    private boolean success;
    private String message;
    private String transactionId;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String phoneNumber;
    private String status;
    private LocalDateTime timestamp;

    public WithdrawResponse() {}

    public WithdrawResponse(boolean success, String message, String transactionId,
                            BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter,
                            String phoneNumber, String status, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.timestamp = timestamp;
    }

    public static WithdrawResponse success(String transactionId, BigDecimal amount,
                                           BigDecimal balanceBefore, BigDecimal balanceAfter,
                                           String phoneNumber) {
        return new WithdrawResponse(true, "Withdrawal successful", transactionId,
                amount, balanceBefore, balanceAfter, phoneNumber, "COMPLETED", LocalDateTime.now());
    }

    public static WithdrawResponse failure(String message) {
        return new WithdrawResponse(false, message, null,
                null, null, null, null, "FAILED", LocalDateTime.now());
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(BigDecimal balanceBefore) { this.balanceBefore = balanceBefore; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}