package com.justbinary.exception;

public class AutoTradingAlreadyRunningException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String userId;

    public AutoTradingAlreadyRunningException(String userId) {
        super(String.format(
            "Auto trading is already running for user [%s]. " +
            "Stop the current session before starting a new one.",
            userId
        ));
        this.userId = userId;
    }

    // ── OVERLOAD — accepts full custom message ───────────────────
    public AutoTradingAlreadyRunningException(String userId,
                                               String message) {
        super(message);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getToastMessage() {
        return "⚠️ Auto trading is already active. " +
               "Stop the current session first.";
    }
}