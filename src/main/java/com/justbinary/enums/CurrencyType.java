package com.justbinary.enums;

public enum CurrencyType {
    USD("US Dollar", "$", "en-US"),
    EUR("Euro", "€", "de-DE"),
    GBP("British Pound", "£", "en-GB"),
    BTC("Bitcoin", "₿", "en-US"),
    USDT("Tether USD", "₮", "en-US");

    private final String displayName;
    private final String symbol;
    private final String locale;

    CurrencyType(String displayName, String symbol, String locale) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.locale = locale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getLocale() {
        return locale;
    }

    public boolean isCrypto() {
        return this == BTC || this == USDT;
    }

    public boolean isFiat() {
        return this == USD || this == EUR || this == GBP;
    }

    public boolean isDefault() {
        return this == USD;
    }

    public static CurrencyType fromSymbol(String symbol) {
        for (CurrencyType type : values()) {
            if (type.symbol.equals(symbol)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown currency symbol: " + symbol);
    }
}