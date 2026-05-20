package com.justbinary.dto;

public class PesapalPaymentRequest {

    private String phone;
    private double amount;
    private String currency;
    private String description;

    public PesapalPaymentRequest() {}

    public PesapalPaymentRequest(String phone, double amount,
                                  String currency, String description) {
        this.phone = phone;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}