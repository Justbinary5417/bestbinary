package com.justbinary.exception;

public class TransactionFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String transactionId;
    private final String transactionType;
    private final double amount;
    private final String reason;

    public TransactionFailedException(String transactionId,
                                       String transactionType,
                                       double amount,
                                       String reason) {
        super(String.format(
            "Transaction failed. " +
            "Transaction ID: [%s] of type: [%s] " +
            "for amount: $%.2f failed due to: %s.",
            transactionId, transactionType, amount, reason
        ));
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.reason = reason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public boolean isDepositTransaction() {
        return "DEPOSIT".equalsIgnoreCase(transactionType);
    }

    public boolean isWithdrawalTransaction() {
        return "WITHDRAWAL".equalsIgnoreCase(transactionType);
    }

    public boolean isZeroAmount() {
        return amount <= 0;
    }

    public boolean hasReason() {
        return reason != null && !reason.trim().isEmpty();
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Transaction [%s] of $%.2f failed. " +
            "Reason: %s",
            transactionType, amount, reason
        );
    }
}