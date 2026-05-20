package com.justbinary.exception;

public class TradeAlreadyResolvedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String tradeId;
    private final String userId;
    private final String resolvedStatus;

    public TradeAlreadyResolvedException(String tradeId,
                                          String userId,
                                          String resolvedStatus) {
        super(String.format(
            "Trade already resolved. " +
            "Trade ID: [%s] for User: [%s] " +
            "was already closed with status: [%s].",
            tradeId, userId, resolvedStatus
        ));
        this.tradeId = tradeId;
        this.userId = userId;
        this.resolvedStatus = resolvedStatus;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getResolvedStatus() {
        return resolvedStatus;
    }

    public boolean isWin() {
        return "WIN".equalsIgnoreCase(resolvedStatus);
    }

    public boolean isLoss() {
        return "LOSS".equalsIgnoreCase(resolvedStatus);
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Trade ID [%s] is already " +
            "closed with status: [%s].",
            tradeId, resolvedStatus
        );
    }
}