package com.justbinary.service;

import com.justbinary.dto.TradeRequest;
import com.justbinary.model.Trade;
import com.justbinary.model.User;
import com.justbinary.model.Wallet;
import com.justbinary.repository.TradeRepository;
import com.justbinary.repository.UserRepository;
import com.justbinary.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeRepository  tradeRepository;
    private final UserRepository   userRepository;
    private final WalletRepository walletRepository;

    public TradeServiceImpl(TradeRepository tradeRepository,
                            UserRepository userRepository,
                            WalletRepository walletRepository) {
        this.tradeRepository  = tradeRepository;
        this.userRepository   = userRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public String placeTrade(String email, TradeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (request.getAmount() == null) {
            throw new RuntimeException("Trade amount is required.");
        }

        double amount  = request.getAmount().doubleValue();
        boolean isReal = "REAL".equalsIgnoreCase(user.getAccountType());

        if (isReal) {
            double realBalance = wallet.getRealBalance() != null ? wallet.getRealBalance() : 0.0;
            if (realBalance < amount) {
                throw new RuntimeException("Insufficient real account balance.");
            }
            // ✅ Deduct stake from real balance — frontend already shows this instantly
            wallet.setRealBalance(realBalance - amount);
        } else {
            double demoBalance = wallet.getDemoBalance() != null ? wallet.getDemoBalance() : 0.0;
            if (demoBalance < amount) {
                throw new RuntimeException("Insufficient demo account balance.");
            }
            wallet.setDemoBalance(demoBalance - amount);
        }

        walletRepository.save(wallet);

        Trade trade = new Trade();
        trade.setUserId(user.getId());
        trade.setAsset(request.getAssetSymbol());
        trade.setAmount(amount);
        trade.setDirection(request.getDirection());
        trade.setDurationSeconds(request.getDurationSeconds());
        trade.setAccountType(user.getAccountType());
        trade.setStatus("ACTIVE");
        trade.setCreatedAt(LocalDateTime.now());
        tradeRepository.save(trade);

        // ✅ NO auto-settle — frontend calls /settle/{id} with real outcome
        // autoSettle removed — frontend determines win/loss from live price

        return trade.getId();
    }

    @Override
    public String settleTrade(String tradeId, boolean isWin) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        // ✅ Skip if already settled — prevents double payout
        if ("WON".equals(trade.getStatus()) ||
            "LOST".equals(trade.getStatus())) {
            return "Trade already settled";
        }

        Wallet wallet = walletRepository.findByUserId(trade.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        double stake  = trade.getAmount();
        double payout = stake * 1.95; // ✅ matches frontend payout calculation exactly

        boolean isReal = "REAL".equalsIgnoreCase(trade.getAccountType());

        if (isWin) {
            if (isReal) {
                double current = wallet.getRealBalance() != null ? wallet.getRealBalance() : 0.0;
                wallet.setRealBalance(current + payout); // ✅ adds full payout to saved balance
            } else {
                double current = wallet.getDemoBalance() != null ? wallet.getDemoBalance() : 0.0;
                wallet.setDemoBalance(current + payout);
            }
            trade.setProfit(+(payout - stake)); // ✅ net profit
            trade.setStatus("WON");
        } else {
            // ✅ Loss — stake already deducted in placeTrade, nothing to add
            trade.setProfit(-stake);
            trade.setStatus("LOST");
        }

        trade.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet); // ✅ persists to MongoDB
        tradeRepository.save(trade);   // ✅ saves trade result

        return isWin ? "Trade won — balance updated" : "Trade lost";
    }

    @Override
    public String getTradeHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Trade> trades = tradeRepository.findByUserId(user.getId());
        return trades.toString();
    }

    @Override
    public String getActiveTrades(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Trade> trades = tradeRepository.findByUserIdAndStatus(user.getId(), "ACTIVE");
        return trades.toString();
    }

    @Override
    public String getTradeById(String email, String tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found"));
        return trade.toString();
    }
}