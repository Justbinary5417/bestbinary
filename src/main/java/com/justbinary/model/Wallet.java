package com.justbinary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "wallets")
public class Wallet {

    @Id
    private String id;
    private String userId;
    private Double realBalance = 0.0;
    private Double demoBalance = 10000.0;
    private Double balance = 0.0;
    private Double totalDeposited = 0.0;
    private Double totalWithdrawn = 0.0;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Wallet() {}

    public String getId()               { return id; }
    public String getUserId()           { return userId; }
    public Double getRealBalance()      { return realBalance != null ? realBalance : (balance != null ? balance : 0.0); }
    public Double getDemoBalance()      { return demoBalance != null ? demoBalance : 10000.0; }
    public Double getBalance()          { return getRealBalance(); }
    public Double getTotalDeposited()   { return totalDeposited; }
    public Double getTotalWithdrawn()   { return totalWithdrawn; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setUserId(String userId)              { this.userId = userId; }
    public void setRealBalance(Double realBalance)    { this.realBalance = realBalance; this.balance = realBalance; }
    public void setDemoBalance(Double demoBalance)    { this.demoBalance = demoBalance; }
    public void setBalance(Double balance)            { this.balance = balance; this.realBalance = balance; }
    public void setTotalDeposited(Double v)           { this.totalDeposited = v; }
    public void setTotalWithdrawn(Double v)           { this.totalWithdrawn = v; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}