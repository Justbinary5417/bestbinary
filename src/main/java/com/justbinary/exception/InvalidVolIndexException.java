package com.justbinary.exception;

public class InvalidVolIndexException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int volIndex;
    private final int minAllowed = 10;
    private final int maxAllowed = 90;

    public InvalidVolIndexException(int volIndex) {
        super(String.format(
            "Invalid vol index: [%d]. " +
            "Vol index must be between %d and %d in steps of 10.",
            volIndex, 10, 90
        ));
        this.volIndex = volIndex;
    }

    public int getVolIndex() {
        return volIndex;
    }

    public int getMinAllowed() {
        return minAllowed;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }

    public boolean isTooLow() {
        return volIndex < minAllowed;
    }

    public boolean isTooHigh() {
        return volIndex > maxAllowed;
    }

    public boolean isInvalidStep() {
        return volIndex % 10 != 0;
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Vol index [%d] is invalid. " +
            "Must be between %d–%d in steps of 10.",
            volIndex, minAllowed, maxAllowed
        );
    }
}