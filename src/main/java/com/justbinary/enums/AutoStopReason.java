package com.justbinary.enums;

public enum AutoStopReason {
    TAKE_PROFIT("Take Profit Hit"),
    STOP_LOSS("Stop Loss Hit"),
    INSUFFICIENT_BALANCE("Insufficient Balance"),
    USER_STOPPED("Stopped by User");

    private final String displayMessage;

    AutoStopReason(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public boolean isUserInitiated() {
        return this == USER_STOPPED;
    }

    public boolean isRiskTriggered() {
        return this == TAKE_PROFIT || this == STOP_LOSS;
    }

    public boolean isForcedStop() {
        return this == INSUFFICIENT_BALANCE;
    }

    public String getToastMessage() {
        switch (this) {
            case TAKE_PROFIT:         return "🏆 TAKE PROFIT HIT!";
            case STOP_LOSS:           return "🛑 STOP LOSS HIT!";
            case INSUFFICIENT_BALANCE:return "❌ Insufficient balance — auto stopped";
            case USER_STOPPED:        return "⏹ Auto trading stopped";
            default:                  return displayMessage;
        }
    }
}