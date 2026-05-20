package com.justbinary.dto.response;

import com.justbinary.enums.AutoStopReason;
import com.justbinary.enums.AccountType;
import com.justbinary.enums.MarketTab;
import com.justbinary.enums.TradeType;
import com.justbinary.enums.VolIndex;
import com.justbinary.enums.TickType;
import com.justbinary.enums.TradeMode;

import java.time.LocalDateTime;

/**
 * ════════════════════════════════════════════════════════
 *  AutoTradingResponse.java
 *  com.justbinary.dto.response
 *
 *  Returned by:
 *    POST /api/auto-trading/start  → session started
 *    POST /api/auto-trading/stop   → session stopped
 *    GET  /api/auto-trading/status → current session state
 *    GET  /api/auto-trading/history → list of past sessions
 * ════════════════════════════════════════════════════════
 */
public class AutoTradingResponse {

    // ── Session Identity ──────────────────────────────
    private Long sessionId;
    private boolean running;

    // ── Configuration (echoed back) ───────────────────
    private AccountType accountType;
    private MarketTab   marketTab;
    private TradeType   tradeType;
    private VolIndex    volIndex;
    private TickType    tickType;
    private TradeMode   tradeMode;
    private double      initialStake;
    private double      currentStake;   // changes with martingale
    private int         maxTrades;
    private double      takeProfit;
    private double      stopLoss;
    private double      multiplier;
    private int         delaySeconds;

    // ── Session Statistics ────────────────────────────
    private int    totalTrades;
    private int    wins;
    private int    losses;
    private double winRate;         // 0.0 – 100.0
    private double totalPnl;        // running profit/loss
    private double startBalance;
    private double currentBalance;

    // ── Timing ───────────────────────────────────────
    private LocalDateTime startTime;
    private LocalDateTime endTime;   // null if still running
    private long          elapsedSeconds;

    // ── Stop Reason (null if still running) ──────────
    private AutoStopReason stopReason;  // TAKE_PROFIT, STOP_LOSS,
                                        // MAX_TRADES, MANUAL,
                                        // INSUFFICIENT_BALANCE

    // ── Message ──────────────────────────────────────
    private String message;

    // ═══════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════

    public AutoTradingResponse() {}

    /** Quick constructor for start/stop responses */
    public AutoTradingResponse(Long sessionId, boolean running, String message) {
        this.sessionId = sessionId;
        this.running   = running;
        this.message   = message;
    }

    /** Full constructor for status polling */
    public AutoTradingResponse(
            Long sessionId,
            boolean running,
            AccountType accountType,
            MarketTab marketTab,
            TradeType tradeType,
            VolIndex volIndex,
            TickType tickType,
            TradeMode tradeMode,
            double initialStake,
            double currentStake,
            int maxTrades,
            double takeProfit,
            double stopLoss,
            double multiplier,
            int delaySeconds,
            int totalTrades,
            int wins,
            int losses,
            double totalPnl,
            double startBalance,
            double currentBalance,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long elapsedSeconds,
            AutoStopReason stopReason,
            String message
    ) {
        this.sessionId      = sessionId;
        this.running        = running;
        this.accountType    = accountType;
        this.marketTab      = marketTab;
        this.tradeType      = tradeType;
        this.volIndex       = volIndex;
        this.tickType       = tickType;
        this.tradeMode      = tradeMode;
        this.initialStake   = initialStake;
        this.currentStake   = currentStake;
        this.maxTrades      = maxTrades;
        this.takeProfit     = takeProfit;
        this.stopLoss       = stopLoss;
        this.multiplier     = multiplier;
        this.delaySeconds   = delaySeconds;
        this.totalTrades    = totalTrades;
        this.wins           = wins;
        this.losses         = losses;
        this.winRate        = totalTrades > 0
                              ? Math.round((wins * 100.0 / totalTrades) * 10.0) / 10.0
                              : 0.0;
        this.totalPnl       = totalPnl;
        this.startBalance   = startBalance;
        this.currentBalance = currentBalance;
        this.startTime      = startTime;
        this.endTime        = endTime;
        this.elapsedSeconds = elapsedSeconds;
        this.stopReason     = stopReason;
        this.message        = message;
    }

    // ═══════════════════════════════════════════════════
    // STATIC FACTORY METHODS
    // (clean way to build responses in AutoTradingService)
    // ═══════════════════════════════════════════════════

    /** Call this in AutoTradingService.start() */
    public static AutoTradingResponse started(Long sessionId) {
        return new AutoTradingResponse(
                sessionId, true, "Auto trading session started successfully"
        );
    }

    /** Call this in AutoTradingService.stop() */
    public static AutoTradingResponse stopped(
            Long sessionId,
            AutoStopReason reason,
            int trades, int wins, int losses,
            double pnl
    ) {
        AutoTradingResponse r = new AutoTradingResponse(
                sessionId, false,
                "Auto trading stopped: " + reason.name()
        );
        r.stopReason  = reason;
        r.totalTrades = trades;
        r.wins        = wins;
        r.losses      = losses;
        r.totalPnl    = pnl;
        r.winRate     = trades > 0
                        ? Math.round((wins * 100.0 / trades) * 10.0) / 10.0
                        : 0.0;
        return r;
    }

    /** Call this in AutoTradingService.getStatus() — no active session */
    public static AutoTradingResponse idle() {
        AutoTradingResponse r = new AutoTradingResponse();
        r.running  = false;
        r.message  = "No active auto trading session";
        return r;
    }

    // ═══════════════════════════════════════════════════
    // GETTERS & SETTERS
    // ═══════════════════════════════════════════════════

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public MarketTab getMarketTab() { return marketTab; }
    public void setMarketTab(MarketTab marketTab) { this.marketTab = marketTab; }

    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }

    public VolIndex getVolIndex() { return volIndex; }
    public void setVolIndex(VolIndex volIndex) { this.volIndex = volIndex; }

    public TickType getTickType() { return tickType; }
    public void setTickType(TickType tickType) { this.tickType = tickType; }

    public TradeMode getTradeMode() { return tradeMode; }
    public void setTradeMode(TradeMode tradeMode) { this.tradeMode = tradeMode; }

    public double getInitialStake() { return initialStake; }
    public void setInitialStake(double initialStake) { this.initialStake = initialStake; }

    public double getCurrentStake() { return currentStake; }
    public void setCurrentStake(double currentStake) { this.currentStake = currentStake; }

    public int getMaxTrades() { return maxTrades; }
    public void setMaxTrades(int maxTrades) { this.maxTrades = maxTrades; }

    public double getTakeProfit() { return takeProfit; }
    public void setTakeProfit(double takeProfit) { this.takeProfit = takeProfit; }

    public double getStopLoss() { return stopLoss; }
    public void setStopLoss(double stopLoss) { this.stopLoss = stopLoss; }

    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }

    public int getDelaySeconds() { return delaySeconds; }
    public void setDelaySeconds(int delaySeconds) { this.delaySeconds = delaySeconds; }

    public int getTotalTrades() { return totalTrades; }
    public void setTotalTrades(int totalTrades) { this.totalTrades = totalTrades; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }

    public double getTotalPnl() { return totalPnl; }
    public void setTotalPnl(double totalPnl) { this.totalPnl = totalPnl; }

    public double getStartBalance() { return startBalance; }
    public void setStartBalance(double startBalance) { this.startBalance = startBalance; }

    public double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public long getElapsedSeconds() { return elapsedSeconds; }
    public void setElapsedSeconds(long elapsedSeconds) { this.elapsedSeconds = elapsedSeconds; }

    public AutoStopReason getStopReason() { return stopReason; }
    public void setStopReason(AutoStopReason stopReason) { this.stopReason = stopReason; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // ═══════════════════════════════════════════════════
    // toString  (useful for logs)
    // ═══════════════════════════════════════════════════
    @Override
    public String toString() {
        return "AutoTradingResponse{" +
                "sessionId=" + sessionId +
                ", running=" + running +
                ", trades=" + totalTrades +
                ", wins=" + wins +
                ", losses=" + losses +
                ", pnl=" + totalPnl +
                ", stopReason=" + stopReason +
                '}';
    }
}