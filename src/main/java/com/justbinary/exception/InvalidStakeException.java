package com.justbinary.exception;

public class InvalidStakeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final double attemptedStake;
    private final double minimumStake = 1.00;
    private final String userId;

    public InvalidStakeException(double attemptedStake,
                                  String userId) {
        super(String.format(
            "Invalid stake amount. " +
            "User: [%s] attempted stake of $%.2f " +
            "which is below minimum of $%.2f.",
            userId, attemptedStake, 1.00
        ));
        this.attemptedStake = attemptedStake;
        this.userId = userId;
    }

    public double getAttemptedStake() {
        return attemptedStake;
    }

    public double getMinimumStake() {
        return minimumStake;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isZeroStake() {
        return attemptedStake <= 0;
    }

    public boolean isBelowMinimum() {
        return attemptedStake < minimumStake;
    }

    public double getDeficit() {
        return minimumStake - attemptedStake;
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Stake of $%.2f is below " +
            "minimum of $%.2f.",
            attemptedStake, minimumStake
        );
    }
}