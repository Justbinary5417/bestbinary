package com.justbinary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "deposits")
public class DepositRecord {

    public enum Status { PENDING, COMPLETED, FAILED }

    @Id
    private String id;
    private String userId;
    private double amount;
    private String phoneNumber;
    private String mpesaCheckoutRequestId;
    private Status status = Status.PENDING;
    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime completedAt;

    public DepositRecord() {}

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getMpesaCheckoutRequestId() { return mpesaCheckoutRequestId; }
    public Status getStatus() { return status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setMpesaCheckoutRequestId(String id) { this.mpesaCheckoutRequestId = id; }
    public void setStatus(Status status) { this.status = status; }
    public void setRequestedAt(LocalDateTime t) { this.requestedAt = t; }
    public void setCompletedAt(LocalDateTime t) { this.completedAt = t; }
}