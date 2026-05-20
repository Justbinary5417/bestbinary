package com.justbinary.exception;

public class StopLossReachedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String userId;
    private final double stopLossTarget;
    private final double actualPnl;

    public StopLossReachedException(String userId,
                                     double stopLossTarget,
                                     double actualPnl) {
        super(String.format(
            "Stop loss of $%.2f triggered for user [%s]. " +
            "Final P&L: -$%.2f. Auto trading stopped.",
            stopLossTarget, userId, Math.abs(actualPnl)
        ));
        this.userId         = userId;
        this.stopLossTarget = stopLossTarget;
        this.actualPnl      = actualPnl;
    }

    // ── OVERLOAD — accepts full custom message ───────────────────
    public StopLossReachedException(String userId,
                                     double stopLossTarget,
                                     double actualPnl,
                                     String message) {
        super(message);
        this.userId         = userId;
        this.stopLossTarget = stopLossTarget;
        this.actualPnl      = actualPnl;
    }

    public String getUserId() {
        return userId;
    }

    public double getStopLossTarget() {
        return stopLossTarget;
    }

    public double getActualPnl() {
        return actualPnl;
    }

    public boolean isFullyDepleted() {
        return actualPnl <= -stopLossTarget;
    }

    public String getToastMessage() {
        return String.format(
            "🛑 Stop loss hit! Target: $%.2f | P&L: -$%.2f",
            stopLossTarget, Math.abs(actualPnl)
        );
    }
}