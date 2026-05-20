package com.justbinary.exception;

public class TradeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String tradeId;
    private final String userId;

    public TradeNotFoundException(String tradeId,
                                   String userId) {
        super(String.format(
            "Trade not found. " +
            "Trade ID: [%s] for User: [%s] " +
            "was not found in positions.",
            tradeId, userId
        ));
        this.tradeId = tradeId;
        this.userId = userId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean hasTradeId() {
        return tradeId != null && !tradeId.trim().isEmpty();
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Trade ID [%s] not found " +
            "in positions.",
            tradeId
        );
    }
}