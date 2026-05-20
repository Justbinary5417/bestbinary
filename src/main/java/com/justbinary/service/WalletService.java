package com.justbinary.service;

import com.justbinary.dto.DepositRequest;
import com.justbinary.dto.WithdrawRequest;

public interface WalletService {
    String getBalance(String email);
    String deposit(String email, DepositRequest request);
    String withdraw(String email, WithdrawRequest request);
    String getTransactionHistory(String email);
    String approveWithdrawal(String withdrawalId);
    void savePendingDeposit(String phoneNumber, double usdAmount,
                            int kesAmount, String checkoutRequestId);
    void confirmDeposit(String checkoutRequestId, double usdAmount,
                        String mpesaReceipt, String phoneNumber);
    void failDeposit(String checkoutRequestId);
}