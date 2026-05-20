package com.justbinary.exception;

public class DemoResetNotAllowedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String accountType;
    private final String userId;
    private final double currentBalance;

    public DemoResetNotAllowedException(String accountType,
                                         String userId,
                                         double currentBalance) {
        super(String.format(
            "Demo reset not allowed. " +
            "Account type: [%s] for User: [%s] " +
            "with balance: $%.2f cannot be reset.",
            accountType, userId, currentBalance
        ));
        this.accountType = accountType;
        this.userId = userId;
        this.currentBalance = currentBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getUserId() {
        return userId;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public boolean isRealAccount() {
        return "REAL".equalsIgnoreCase(accountType);
    }

    public boolean hasPositiveBalance() {
        return currentBalance > 0;
    }

    public boolean isBalanceDepleted() {
        return currentBalance <= 0;
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Reset not allowed on [%s] account. " +
            "Current balance: $%.2f",
            accountType, currentBalance
        );
    }
}