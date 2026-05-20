package com.justbinary.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.justbinary.repository.TradeRepository;
import com.justbinary.repository.WalletRepository;
import com.justbinary.repository.UserRepository;
import com.justbinary.model.Wallet;
import com.justbinary.exception.InsufficientBalanceException;
import com.justbinary.exception.AutoTradingAlreadyRunningException;
import com.justbinary.exception.AutoTradingNotRunningException;
import com.justbinary.exception.TakeProfitReachedException;
import com.justbinary.exception.StopLossReachedException;

@Service
public class AutoTradingService {

    private final TradeRepository  tradeRepository;
    private final WalletRepository walletRepository;
    private final UserRepository   userRepository;

    private final Map<String, AutoSession> activeSessions = new ConcurrentHashMap<>();

    public AutoTradingService(TradeRepository tradeRepository,
                              WalletRepository walletRepository,
                              UserRepository userRepository) {
        this.tradeRepository  = tradeRepository;
        this.walletRepository = walletRepository;
        this.userRepository   = userRepository;
    }

    // ─── INNER CLASS ──────────────────────────────────────────
    public static class AutoSession {
        private final String userId;
        private final String tradeType;
        private final String market;
        private final double stake;
        private final double takeProfit;
        private final double stopLoss;
        private double  currentBalance;
        private double  pnl    = 0;
        private boolean running = true;
        private AutoStopReason stopReason = null;

        public AutoSession(String userId, String tradeType, String market,
                           double stake, double takeProfit, double stopLoss,
                           double currentBalance) {
            this.userId         = userId;
            this.tradeType      = tradeType;
            this.market         = market;
            this.stake          = stake;
            this.takeProfit     = takeProfit;
            this.stopLoss       = stopLoss;
            this.currentBalance = currentBalance;
        }

        public void stop(AutoStopReason reason) {
            this.running    = false;
            this.stopReason = reason;
        }

        public void recordWin(double profit) {
            this.pnl            += profit;
            this.currentBalance += profit;
        }

        public void recordLoss(double loss) {
            this.pnl            -= loss;
            this.currentBalance -= loss;
        }

        public boolean        isRunning()         { return running; }
        public double         getPnl()            { return pnl; }
        public double         getStake()          { return stake; }
        public double         getTakeProfit()     { return takeProfit; }
        public double         getStopLoss()       { return stopLoss; }
        public double         getCurrentBalance() { return currentBalance; }
        public String         getUserId()         { return userId; }
        public String         getTradeType()      { return tradeType; }
        public String         getMarket()         { return market; }
        public AutoStopReason getStopReason()     { return stopReason; }
    }

    public enum AutoStopReason {
        MANUAL, TAKE_PROFIT, STOP_LOSS, INSUFFICIENT_BALANCE
    }

    // ─── START AUTO TRADING ───────────────────────────────────
    public AutoSession startAutoTrading(String userId, String tradeType,
                                        String market, double stake,
                                        double takeProfit, double stopLoss) {

        if (activeSessions.containsKey(userId)
                && activeSessions.get(userId).isRunning()) {
            throw new AutoTradingAlreadyRunningException(
                    "User [" + userId + "] already has an active session.");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new InsufficientBalanceException(
                        userId, stake, 0));

        double currentBalance = wallet.getBalance();

        if (currentBalance < stake) {
            throw new InsufficientBalanceException(userId, stake, currentBalance);
        }

        AutoSession session = new AutoSession(
                userId, tradeType, market,
                stake, takeProfit, stopLoss,
                currentBalance);

        activeSessions.put(userId, session);
        return session;
    }

    // ─── RECORD TRADE RESULT ──────────────────────────────────
    public AutoSession recordTradeResult(String userId, double payout,
                                          double stake, boolean won) {

        AutoSession session = getActiveSession(userId);

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new InsufficientBalanceException(
                        userId, stake, 0));

        if (won) {
            double profit = payout - stake;
            wallet.setBalance(wallet.getBalance() + profit);
            session.recordWin(profit);
        } else {
            wallet.setBalance(wallet.getBalance() - stake);
            session.recordLoss(stake);
        }

        walletRepository.save(wallet);

        double currentBalance = wallet.getBalance();
        double pnl            = session.getPnl();

        if (pnl >= session.getTakeProfit()) {
            session.stop(AutoStopReason.TAKE_PROFIT);
            activeSessions.remove(userId);
            throw new TakeProfitReachedException(userId, session.getTakeProfit(), pnl);
        }

        if (pnl <= -session.getStopLoss()) {
            session.stop(AutoStopReason.STOP_LOSS);
            activeSessions.remove(userId);
            throw new StopLossReachedException(userId, session.getStopLoss(), pnl);
        }

        if (currentBalance < session.getStake()) {
            session.stop(AutoStopReason.INSUFFICIENT_BALANCE);
            activeSessions.remove(userId);
            throw new InsufficientBalanceException(userId, session.getStake(), currentBalance);
        }

        return session;
    }

    // ─── STOP AUTO TRADING ────────────────────────────────────
    public AutoSession stopAutoTrading(String userId) {
        AutoSession session = getActiveSession(userId);
        session.stop(AutoStopReason.MANUAL);
        activeSessions.remove(userId);
        return session;
    }

    // ─── GET SESSION STATUS ───────────────────────────────────
    public AutoSession getSessionStatus(String userId) {
        return getActiveSession(userId);
    }

    public boolean isAutoTradingActive(String userId) {
        return activeSessions.containsKey(userId)
                && activeSessions.get(userId).isRunning();
    }

    // ─── PRIVATE HELPERS ──────────────────────────────────────
    private AutoSession getActiveSession(String userId) {
        AutoSession session = activeSessions.get(userId);
        if (session == null || !session.isRunning()) {
            throw new AutoTradingNotRunningException(
                    "No active auto-trading session for user: " + userId);
        }
        return session;
    }
}