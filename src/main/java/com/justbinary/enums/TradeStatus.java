package com.justbinary.enums;

public enum TradeStatus {
    PENDING,
    OPEN,
    RESOLVED,
    CANCELLED,
    FAILED;

    public boolean isActive() {
        return this == PENDING || this == OPEN;
    }

    public boolean isTerminal() {
        return this == RESOLVED || this == CANCELLED || this == FAILED;
    }
}