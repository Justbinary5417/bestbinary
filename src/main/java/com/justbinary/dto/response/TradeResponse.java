package com.justbinary.dto.response;

import com.justbinary.enums.TradeType;
import com.justbinary.enums.TradeMode;
import com.justbinary.enums.TradeStatus;
import com.justbinary.enums.TickType;
import com.justbinary.enums.TradeOutcomeEnumerated;

import java.time.LocalDateTime;

public class TradeResponse {

    private Long tradeId;
    private Long userId;
    private String symbol;
    private double stake;
    private double payout;
    private double profit;
    private TradeType tradeType;
    private TradeMode tradeMode;
    private TradeStatus tradeStatus;
    private TickType tickType;
    private TradeOutcomeEnumerated tradeOutcome;
    private boolean won;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private String sessionId;

    public TradeResponse() {}

    public TradeResponse(Long tradeId, Long userId, String symbol,
                         double stake, double payout, double profit,
                         TradeType tradeType, TradeMode tradeMode,
                         TradeStatus tradeStatus, TickType tickType,
                         TradeOutcomeEnumerated tradeOutcome, boolean won,
                         LocalDateTime openTime, LocalDateTime closeTime,
                         String sessionId) {
        this.tradeId = tradeId;
        this.userId = userId;
        this.symbol = symbol;
        this.stake = stake;
        this.payout = payout;
        this.profit = profit;
        this.tradeType = tradeType;
        this.tradeMode = tradeMode;
        this.tradeStatus = tradeStatus;
        this.tickType = tickType;
        this.tradeOutcome = tradeOutcome;
        this.won = won;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.sessionId = sessionId;
    }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getStake() { return stake; }
    public void setStake(double stake) { this.stake = stake; }

    public double getPayout() { return payout; }
    public void setPayout(double payout) { this.payout = payout; }

    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }

    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }

    public TradeMode getTradeMode() { return tradeMode; }
    public void setTradeMode(TradeMode tradeMode) { this.tradeMode = tradeMode; }

    public TradeStatus getTradeStatus() { return tradeStatus; }
    public void setTradeStatus(TradeStatus tradeStatus) { this.tradeStatus = tradeStatus; }

    public TickType getTickType() { return tickType; }
    public void setTickType(TickType tickType) { this.tickType = tickType; }

    public TradeOutcomeEnumerated getTradeOutcome() { return tradeOutcome; }
    public void setTradeOutcome(TradeOutcomeEnumerated tradeOutcome) { this.tradeOutcome = tradeOutcome; }

    public boolean isWon() { return won; }
    public void setWon(boolean won) { this.won = won; }

    public LocalDateTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalDateTime openTime) { this.openTime = openTime; }

    public LocalDateTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalDateTime closeTime) { this.closeTime = closeTime; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}