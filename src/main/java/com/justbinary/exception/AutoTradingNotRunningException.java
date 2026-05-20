package com.justbinary.exception;

public class AutoTradingNotRunningException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String userId;

    public AutoTradingNotRunningException(String userId) {
        super(String.format(
            "No active auto trading session found for user [%s]. " +
            "Please start an auto trading session first.",
            userId
        ));
        this.userId = userId;
    }

    // ── OVERLOAD — accepts full custom message ───────────────────
    public AutoTradingNotRunningException(String userId,
                                          String message) {
        super(message);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getToastMessage() {
        return "⚠️ No active auto trading session. " +
               "Please start auto trading first.";
    }
}