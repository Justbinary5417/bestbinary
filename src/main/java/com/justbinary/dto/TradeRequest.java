package com.justbinary.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class TradeRequest {

    private String assetSymbol;      // e.g. BTC/USD, EUR/USD
    private String direction;        // CALL or PUT
    private BigDecimal amount;       // stake amount
    private int durationSeconds;     // e.g. 30, 60, 120, 300

    public TradeRequest() {}

    public TradeRequest(String assetSymbol, String direction, BigDecimal amount, int durationSeconds) {
        this.assetSymbol = assetSymbol;
        this.direction = direction;
        this.amount = amount;
        this.durationSeconds = durationSeconds;
    }

    public String getAssetSymbol() { return assetSymbol; }
    public void setAssetSymbol(String assetSymbol) { this.assetSymbol = assetSymbol; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeRequest)) return false;
        TradeRequest that = (TradeRequest) o;
        return durationSeconds == that.durationSeconds &&
               Objects.equals(assetSymbol, that.assetSymbol) &&
               Objects.equals(direction, that.direction) &&
               Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() { return Objects.hash(assetSymbol, direction, amount, durationSeconds); }

    @Override
    public String toString() {
        return "TradeRequest{assetSymbol='" + assetSymbol + "', direction='" + direction +
               "', amount=" + amount + ", durationSeconds=" + durationSeconds + "}";
    }
}