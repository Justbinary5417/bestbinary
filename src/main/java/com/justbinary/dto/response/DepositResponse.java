package com.justbinary.dto.response;

import com.justbinary.enums.AccountType;
import com.justbinary.enums.CurrencyType;
import com.justbinary.enums.TransactionType;

import java.time.LocalDateTime;

/**
 * ════════════════════════════════════════════════════════
 *  DepositResponse.java
 *  com.justbinary.dto.response
 *
 *  Returned by:
 *    POST /api/wallet/deposit          → initiate deposit
 *    GET  /api/wallet/deposit/{id}/status → poll M-Pesa status
 *
 *  Covers:
 *    - M-Pesa STK Push response
 *    - Card/manual deposit response
 *    - Status polling response
 * ════════════════════════════════════════════════════════
 */
public class DepositResponse {

    // ── Transaction Identity ──────────────────────────
    private String transactionId;       // internal DB id
    private String mpesaReceiptNumber;  // M-Pesa confirmation code
    private String checkoutRequestId;   // M-Pesa STK push request ID
    private String merchantRequestId;   // M-Pesa merchant request ID

    // ── Status ────────────────────────────────────────
    /**
     * PENDING   – STK push sent, waiting for user to enter PIN
     * PROCESSING– PIN entered, awaiting M-Pesa confirmation
     * COMPLETED – Money received, balance updated
     * FAILED    – Transaction failed or timed out
     * CANCELLED – User cancelled on phone
     */
    private String status;

    // ── Amount Info ───────────────────────────────────
    private double       amount;
    private double       amountKes;      // KES equivalent if depositing in USD
    private CurrencyType currency;
    private String       depositMethod;  // "MPESA" | "CARD" | "BANK"

    // ── Account Info ──────────────────────────────────
    private AccountType accountType;
    private double      balanceBefore;
    private double      balanceAfter;   // updated only when COMPLETED

    // ── M-Pesa Specific ───────────────────────────────
    private String phone;               // phone that received STK push
    private String mpesaMessage;        // message from M-Pesa API

    // ── Timing ───────────────────────────────────────
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;  // null until COMPLETED
    private int           expiresInSeconds; // STK push expiry (default 60s)

    // ── UI Helpers ────────────────────────────────────
    private boolean success;
    private String  message;            // user-friendly message
    private String  instruction;        // e.g. "Check your phone and enter M-Pesa PIN"

    // ═══════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════

    public DepositResponse() {}

    // ═══════════════════════════════════════════════════
    // STATIC FACTORY METHODS
    // ═══════════════════════════════════════════════════

    /**
     * Call in WalletService/MpesaService after STK push sent successfully.
     *
     * Usage in MpesaService.stkPush():
     *   return DepositResponse.stkPushSent(
     *       transactionId,
     *       checkoutRequestId,
     *       merchantRequestId,
     *       phone,
     *       amount,
     *       amountKes,
     *       AccountType.REAL,
     *       wallet.getRealBalance()
     *   );
     */
    public static DepositResponse stkPushSent(
            String transactionId,
            String checkoutRequestId,
            String merchantRequestId,
            String phone,
            double amount,
            double amountKes,
            AccountType accountType,
            double balanceBefore
    ) {
        DepositResponse r = new DepositResponse();
        r.transactionId      = transactionId;
        r.checkoutRequestId  = checkoutRequestId;
        r.merchantRequestId  = merchantRequestId;
        r.phone              = phone;
        r.amount             = amount;
        r.amountKes          = amountKes;
        r.currency           = CurrencyType.USD;
        r.depositMethod      = "MPESA";
        r.accountType        = accountType;
        r.balanceBefore      = balanceBefore;
        r.balanceAfter       = balanceBefore; // unchanged until confirmed
        r.status             = "PENDING";
        r.initiatedAt        = LocalDateTime.now();
        r.expiresInSeconds   = 60;
        r.success            = true;
        r.message            = "STK push sent to " + phone;
        r.instruction        = "Check your phone and enter your M-Pesa PIN to complete the deposit.";
        return r;
    }

    /**
     * Call when M-Pesa callback confirms payment received.
     * Updates balance and marks COMPLETED.
     *
     * Usage in MpesaService.handleCallback():
     *   return DepositResponse.completed(
     *       transactionId,
     *       mpesaReceiptNumber,
     *       phone, amount, amountKes,
     *       AccountType.REAL,
     *       balanceBefore,
     *       balanceAfter
     *   );
     */
    public static DepositResponse completed(
            String transactionId,
            String mpesaReceiptNumber,
            String phone,
            double amount,
            double amountKes,
            AccountType accountType,
            double balanceBefore,
            double balanceAfter
    ) {
        DepositResponse r = new DepositResponse();
        r.transactionId      = transactionId;
        r.mpesaReceiptNumber = mpesaReceiptNumber;
        r.phone              = phone;
        r.amount             = amount;
        r.amountKes          = amountKes;
        r.currency           = CurrencyType.USD;
        r.depositMethod      = "MPESA";
        r.accountType        = accountType;
        r.balanceBefore      = balanceBefore;
        r.balanceAfter       = balanceAfter;
        r.status             = "COMPLETED";
        r.initiatedAt        = LocalDateTime.now();
        r.completedAt        = LocalDateTime.now();
        r.success            = true;
        r.message            = "Deposit of $" + amount + " confirmed! Receipt: " + mpesaReceiptNumber;
        r.instruction        = null;
        return r;
    }

    /**
     * Call when deposit fails or times out.
     *
     * Usage:
     *   return DepositResponse.failed(transactionId, "Transaction cancelled by user");
     */
    public static DepositResponse failed(
            String transactionId,
            String reason
    ) {
        DepositResponse r = new DepositResponse();
        r.transactionId = transactionId;
        r.status        = "FAILED";
        r.success       = false;
        r.message       = "Deposit failed: " + reason;
        r.instruction   = "Please try again or contact support.";
        r.initiatedAt   = LocalDateTime.now();
        return r;
    }

    /**
     * Call when polling status — returns current state.
     *
     * Usage in WalletService.checkDepositStatus():
     *   return DepositResponse.statusCheck(transaction);
     */
    public static DepositResponse pending(String transactionId, String phone) {
        DepositResponse r = new DepositResponse();
        r.transactionId    = transactionId;
        r.phone            = phone;
        r.status           = "PENDING";
        r.success          = true;
        r.message          = "Waiting for M-Pesa confirmation...";
        r.instruction      = "Please enter your M-Pesa PIN if prompted.";
        r.expiresInSeconds = 60;
        return r;
    }

    // ═══════════════════════════════════════════════════
    // GETTERS & SETTERS
    // ═══════════════════════════════════════════════════

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMpesaReceiptNumber() { return mpesaReceiptNumber; }
    public void setMpesaReceiptNumber(String mpesaReceiptNumber) { this.mpesaReceiptNumber = mpesaReceiptNumber; }

    public String getCheckoutRequestId() { return checkoutRequestId; }
    public void setCheckoutRequestId(String checkoutRequestId) { this.checkoutRequestId = checkoutRequestId; }

    public String getMerchantRequestId() { return merchantRequestId; }
    public void setMerchantRequestId(String merchantRequestId) { this.merchantRequestId = merchantRequestId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getAmountKes() { return amountKes; }
    public void setAmountKes(double amountKes) { this.amountKes = amountKes; }

    public CurrencyType getCurrency() { return currency; }
    public void setCurrency(CurrencyType currency) { this.currency = currency; }

    public String getDepositMethod() { return depositMethod; }
    public void setDepositMethod(String depositMethod) { this.depositMethod = depositMethod; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public double getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(double balanceBefore) { this.balanceBefore = balanceBefore; }

    public double getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMpesaMessage() { return mpesaMessage; }
    public void setMpesaMessage(String mpesaMessage) { this.mpesaMessage = mpesaMessage; }

    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public int getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(int expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }

    // ═══════════════════════════════════════════════════
    // toString
    // ═══════════════════════════════════════════════════
    @Override
    public String toString() {
        return "DepositResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", amountKes=" + amountKes +
                ", phone='" + phone + '\'' +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                ", success=" + success +
                '}';
    }
}