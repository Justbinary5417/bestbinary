package com.justbinary.enums;

public enum TransactionType {
    STAKE("Stake"),
    PAYOUT("Payout"),
    DEPOSIT("Deposit"),
    RESET("Demo Reset");

    private final String displayLabel;

    TransactionType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public boolean isDebit() {
        return this == STAKE;
    }

    public boolean isCredit() {
        return this == PAYOUT || this == DEPOSIT || this == RESET;
    }

    public boolean isDemo() {
        return this == RESET;
    }

    public boolean affectsBalance() {
        return true;
    }
}