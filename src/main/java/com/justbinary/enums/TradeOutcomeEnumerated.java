package com.justbinary.enums;

public enum TradeOutcomeEnumerated {
    WIN,
    LOSS,
    PUSH,
    PENDING;

    public boolean isProfitable() {
        return this == WIN;
    }

    public boolean isSettled() {
        return this == WIN || this == LOSS || this == PUSH;
    }

    public String getDisplayLabel() {
        switch (this) {
            case WIN:     return "✅ Win";
            case LOSS:    return "❌ Loss";
            case PUSH:    return "➖ Push";
            case PENDING: return "⏳ Pending";
            default:      return "Unknown";
        }
    }
}