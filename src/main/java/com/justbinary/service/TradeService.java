package com.justbinary.service;

import com.justbinary.dto.TradeRequest;

public interface TradeService {
    String placeTrade(String email, TradeRequest request);
    String settleTrade(String tradeId, boolean isWin); // ✅ ADD THIS
    String getTradeHistory(String email);
    String getActiveTrades(String email);
    String getTradeById(String email, String tradeId);
}