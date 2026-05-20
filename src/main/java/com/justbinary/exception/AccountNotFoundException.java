package com.justbinary.exception;

public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String accountType;
    private final String userId;

    public AccountNotFoundException(String accountType,
                                     String userId) {
        super(String.format(
            "Account not found. " +
            "Account type: [%s] for User: [%s] " +
            "does not exist.",
            accountType, userId
        ));
        this.accountType = accountType;
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isDemoAccount() {
        return "DEMO".equalsIgnoreCase(accountType);
    }

    public boolean isRealAccount() {
        return "REAL".equalsIgnoreCase(accountType);
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Account type [%s] not found " +
            "for User: [%s].",
            accountType, userId
        );
    }
}