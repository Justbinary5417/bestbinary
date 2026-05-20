package com.justbinary.exception;

public class DepositFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String userId;
    private final double depositAmount;
    private final String paymentMethod;
    private final String failureReason;

    public DepositFailedException(String userId,
                                   double depositAmount,
                                   String paymentMethod,
                                   String failureReason) {
        super(String.format(
            "Deposit failed. " +
            "User: [%s] attempted deposit of $%.2f " +
            "via [%s] failed due to: %s.",
            userId, depositAmount, paymentMethod, failureReason
        ));
        this.userId = userId;
        this.depositAmount = depositAmount;
        this.paymentMethod = paymentMethod;
        this.failureReason = failureReason;
    }

    public String getUserId() {
        return userId;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public boolean isZeroAmount() {
        return depositAmount <= 0;
    }

    public boolean isBelowMinimum() {
        return depositAmount < 10.00;
    }

    public boolean hasPaymentMethod() {
        return paymentMethod != null && !paymentMethod.trim().isEmpty();
    }

    public boolean hasFailureReason() {
        return failureReason != null && !failureReason.trim().isEmpty();
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Deposit of $%.2f via [%s] could not be completed. " +
            "Reason: %s",
            depositAmount, paymentMethod, failureReason
        );
    }
}