package com.justbinary.dto.response;

public class PesapalPaymentResponse {

    private boolean success;
    private String transactionId;
    private String redirectUrl;
    private String status;
    private String message;

    public PesapalPaymentResponse() {}

    public PesapalPaymentResponse(boolean success, String transactionId,
                                   String redirectUrl, String status,
                                   String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.redirectUrl = redirectUrl;
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}