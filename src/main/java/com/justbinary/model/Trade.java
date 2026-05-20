package com.justbinary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "trades")
public class Trade {

    @Id
    private String id;
    private String userId;
    private String asset;
    private String direction;
    private Double amount;
    private Integer durationSeconds;
    private Double entryPrice;
    private Double exitPrice;
    private String status;
    private Double payout;
    private String accountType;
    private Double profit;                                    // ✅ ADDED — fixes line 108 & 112
    private LocalDateTime updatedAt;                         // ✅ ADDED — fixes line 116
    private LocalDateTime createdAt = LocalDateTime.now();

    public Trade() {}

    // ── GETTERS ──
    public String getId()               { return id; }
    public String getUserId()           { return userId; }
    public String getAsset()            { return asset; }
    public String getDirection()        { return direction; }
    public Double getAmount()           { return amount; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public Double getEntryPrice()       { return entryPrice; }
    public Double getExitPrice()        { return exitPrice; }
    public String getStatus()           { return status; }
    public Double getPayout()           { return payout; }
    public String getAccountType()      { return accountType; }
    public Double getProfit()           { return profit; }       // ✅ ADDED
    public LocalDateTime getUpdatedAt() { return updatedAt; }   // ✅ ADDED
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── SETTERS ──
    public void setId(String id)                      { this.id = id; }
    public void setUserId(String userId)              { this.userId = userId; }
    public void setAsset(String asset)                { this.asset = asset; }
    public void setDirection(String direction)        { this.direction = direction; }
    public void setAmount(Double amount)              { this.amount = amount; }
    public void setDurationSeconds(Integer d)         { this.durationSeconds = d; }
    public void setEntryPrice(Double entryPrice)      { this.entryPrice = entryPrice; }
    public void setExitPrice(Double exitPrice)        { this.exitPrice = exitPrice; }
    public void setStatus(String status)              { this.status = status; }
    public void setPayout(Double payout)              { this.payout = payout; }
    public void setAccountType(String accountType)    { this.accountType = accountType; }
    public void setProfit(Double profit)              { this.profit = profit; }     // ✅ ADDED
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; } // ✅ ADDED
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}