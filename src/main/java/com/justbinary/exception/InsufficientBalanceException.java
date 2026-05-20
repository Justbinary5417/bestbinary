package com.justbinary.exception;

public class InsufficientBalanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String userId;
    private final double requestedStake;
    private final double currentBalance;

    public InsufficientBalanceException(String userId,
                                         double requestedStake,
                                         double currentBalance) {
        super(String.format(
            "Insufficient balance. " +
            "User: [%s] requested stake of $%.2f " +
            "but current balance is $%.2f.",
            userId, requestedStake, currentBalance
        ));
        this.userId = userId;
        this.requestedStake = requestedStake;
        this.currentBalance = currentBalance;
    }

    public String getUserId() {
        return userId;
    }

    public double getRequestedStake() {
        return requestedStake;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public boolean isBalanceDepleted() {
        return currentBalance <= 0;
    }

    public double getDeficit() {
        return requestedStake - currentBalance;
    }

    public boolean canPartiallyFund() {
        return currentBalance > 0 && currentBalance < requestedStake;
    }

    public String getToastMessage() {
        return String.format(
            "❌ Insufficient balance. " +
            "Need $%.2f but only $%.2f available.",
            requestedStake, currentBalance
        );
    }
}