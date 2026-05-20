package com.justbinary.enums;

/**
 * Represents all binary trade contract types supported by JustBinary.
 * Maps directly to the trade button types rendered in the live trade UI.
 */
public enum TradeType {

    // ── Even / Odd ──────────────────────────────────────────
    EVEN,       // Win if last digit of price is even (0,2,4,6,8)
    ODD,        // Win if last digit of price is odd  (1,3,5,7,9)

    // ── Over / Under ─────────────────────────────────────────
    OVER,       // Win if last digit is strictly > 4  (5,6,7,8,9)
    UNDER,      // Win if last digit is strictly < 5  (0,1,2,3,4)

    // ── Matches / Differs ────────────────────────────────────
    MATCHES,    // Win if last digit == entry digit (exact match)
    DIFFERS;    // Win if last digit != entry digit (any mismatch)

    // ── Grouping helpers ─────────────────────────────────────

    /** Returns true if this type belongs to the Even/Odd market tab. */
    public boolean isEvenOdd() {
        return this == EVEN || this == ODD;
    }

    /** Returns true if this type belongs to the Over/Under market tab. */
    public boolean isOverUnder() {
        return this == OVER || this == UNDER;
    }

    /** Returns true if this type belongs to the Matches/Differs market tab. */
    public boolean isMatchesDiffers() {
        return this == MATCHES || this == DIFFERS;
    }

    // ── Win resolution ───────────────────────────────────────

    /**
     * Evaluates whether this trade type wins given the last digit
     * of the current price and the last digit at entry.
     *
     * @param lastDigit   last digit of the current tick price (0–9)
     * @param entryDigit  last digit of the price when the trade was placed (0–9)
     * @return true if the contract wins
     */
    public boolean isWin(int lastDigit, int entryDigit) {
        return switch (this) {
            case EVEN    -> lastDigit % 2 == 0;
            case ODD     -> lastDigit % 2 != 0;
            case OVER    -> lastDigit > 4;
            case UNDER   -> lastDigit < 5;
            case MATCHES -> lastDigit == entryDigit;
            case DIFFERS -> lastDigit != entryDigit;
        };
    }

    // ── Display label ────────────────────────────────────────

    /** Returns the human-readable button label shown in the UI. */
    public String displayLabel() {
        return switch (this) {
            case EVEN    -> "Even";
            case ODD     -> "Odd";
            case OVER    -> "Over 4";
            case UNDER   -> "Under 5";
            case MATCHES -> "Matches";
            case DIFFERS -> "Differs";
        };
    }

    // ── Reverse lookup ───────────────────────────────────────

    /**
     * Parses a lowercase string from the frontend (e.g. "even", "over")
     * into the corresponding TradeType.
     *
     * @throws IllegalArgumentException if the string doesn't match any type
     */
    public static TradeType fromKey(String key) {
        return switch (key.toLowerCase().trim()) {
            case "even"    -> EVEN;
            case "odd"     -> ODD;
            case "over"    -> OVER;
            case "under"   -> UNDER;
            case "matches" -> MATCHES;
            case "differs" -> DIFFERS;
            default -> throw new IllegalArgumentException("Unknown trade type: " + key);
        };
    }
}