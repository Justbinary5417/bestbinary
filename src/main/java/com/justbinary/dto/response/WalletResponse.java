package com.justbinary.dto.response;

import com.justbinary.enums.CurrencyType;
import com.justbinary.enums.AccountType;
import com.justbinary.enums.TransactionType;

import java.time.LocalDateTime;

public class WalletResponse {

    private Long walletId;
    private Long userId;
    private String username;
    private double balance;
    private double equity;
    private double margin;
    private double freeMargin;
    private CurrencyType currencyType;
    private AccountType accountType;
    private TransactionType lastTransactionType;
    private double lastTransactionAmount;
    private LocalDateTime lastUpdated;
    private boolean active;

    public WalletResponse() {}

    public WalletResponse(Long walletId, Long userId, String username,
                          double balance, double equity, double margin,
                          double freeMargin, CurrencyType currencyType,
                          AccountType accountType,
                          TransactionType lastTransactionType,
                          double lastTransactionAmount,
                          LocalDateTime lastUpdated, boolean active) {
        this.walletId = walletId;
        this.userId = userId;
        this.username = username;
        this.balance = balance;
        this.equity = equity;
        this.margin = margin;
        this.freeMargin = freeMargin;
        this.currencyType = currencyType;
        this.accountType = accountType;
        this.lastTransactionType = lastTransactionType;
        this.lastTransactionAmount = lastTransactionAmount;
        this.lastUpdated = lastUpdated;
        this.active = active;
    }

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public double getEquity() { return equity; }
    public void setEquity(double equity) { this.equity = equity; }

    public double getMargin() { return margin; }
    public void setMargin(double margin) { this.margin = margin; }

    public double getFreeMargin() { return freeMargin; }
    public void setFreeMargin(double freeMargin) { this.freeMargin = freeMargin; }

    public CurrencyType getCurrencyType() { return currencyType; }
    public void setCurrencyType(CurrencyType currencyType) { this.currencyType = currencyType; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public TransactionType getLastTransactionType() { return lastTransactionType; }
    public void setLastTransactionType(TransactionType lastTransactionType) { this.lastTransactionType = lastTransactionType; }

    public double getLastTransactionAmount() { return lastTransactionAmount; }
    public void setLastTransactionAmount(double lastTransactionAmount) { this.lastTransactionAmount = lastTransactionAmount; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}