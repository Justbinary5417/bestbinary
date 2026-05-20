package com.justbinary.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class WithdrawRequest {

    private String phoneNumber;
    private BigDecimal amount;
    private String method;
    private String accountDetails;

    public WithdrawRequest() {}

    public WithdrawRequest(String phoneNumber, BigDecimal amount, String method, String accountDetails) {
        this.phoneNumber    = phoneNumber;
        this.amount         = amount;
        this.method         = method;
        this.accountDetails = accountDetails;
    }

    public String getPhoneNumber()               { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber){ this.phoneNumber = phoneNumber; }

    public BigDecimal getAmount()                { return amount; }
    public void setAmount(BigDecimal amount)     { this.amount = amount; }

    public String getMethod()                    { return method; }
    public void setMethod(String method)         { this.method = method; }

    public String getAccountDetails()                        { return accountDetails; }
    public void setAccountDetails(String accountDetails)     { this.accountDetails = accountDetails; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WithdrawRequest)) return false;
        WithdrawRequest that = (WithdrawRequest) o;
        return Objects.equals(phoneNumber, that.phoneNumber)
            && Objects.equals(amount, that.amount)
            && Objects.equals(method, that.method)
            && Objects.equals(accountDetails, that.accountDetails);
    }

    @Override
    public int hashCode() { return Objects.hash(phoneNumber, amount, method, accountDetails); }

    @Override
    public String toString() {
        return "WithdrawRequest{phoneNumber='" + phoneNumber + "', amount=" + amount
             + ", method='" + method + "', accountDetails='" + accountDetails + "'}";
    }
}