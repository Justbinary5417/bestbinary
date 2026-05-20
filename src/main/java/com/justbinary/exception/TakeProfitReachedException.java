package com.justbinary.exception;

public class TakeProfitReachedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String userId;
    private final double takeProfitTarget;
    private final double actualPnl;

    public TakeProfitReachedException(String userId,
                                       double takeProfitTarget,
                                       double actualPnl) {
        super(String.format(
            "Take profit target of $%.2f reached for user [%s]. " +
            "Final P&L: +$%.2f. Auto trading stopped.",
            takeProfitTarget, userId, actualPnl
        ));
        this.userId           = userId;
        this.takeProfitTarget = takeProfitTarget;
        this.actualPnl        = actualPnl;
    }

    // ── OVERLOAD — accepts full custom message ───────────────────
    public TakeProfitReachedException(String userId,
                                       double takeProfitTarget,
                                       double actualPnl,
                                       String message) {
        super(message);
        this.userId           = userId;
        this.takeProfitTarget = takeProfitTarget;
        this.actualPnl        = actualPnl;
    }

    public String getUserId() {
        return userId;
    }

    public double getTakeProfitTarget() {
        return takeProfitTarget;
    }

    public double getActualPnl() {
        return actualPnl;
    }

    public String getToastMessage() {
        return String.format(
            "🏆 Take profit hit! Target: $%.2f | P&L: +$%.2f",
            takeProfitTarget, actualPnl
        );
    }
}