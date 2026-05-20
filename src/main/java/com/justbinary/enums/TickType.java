package com.justbinary.enums;

public enum TickType {
    ONE_SECOND("1s"),
    TICK("Tk");

    private final String displayLabel;

    TickType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static TickType fromLabel(String label) {
        for (TickType type : values()) {
            if (type.displayLabel.equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TickType label: " + label);
    }

    public boolean isOneSec() {
        return this == ONE_SECOND;
    }

    public boolean isTick() {
        return this == TICK;
    }
}