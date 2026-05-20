package com.justbinary.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class DepositRequest {

    private String phoneNumber;
    private BigDecimal amount;

    public DepositRequest() {}

    public DepositRequest(String phoneNumber, BigDecimal amount) {
        this.phoneNumber = phoneNumber;
        this.amount = amount;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepositRequest)) return false;
        DepositRequest that = (DepositRequest) o;
        return Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() { return Objects.hash(phoneNumber, amount); }

    @Override
    public String toString() {
        return "DepositRequest{phoneNumber='" + phoneNumber + "', amount=" + amount + "}";
    }
}