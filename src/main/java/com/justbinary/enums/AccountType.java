package com.justbinary.enums;

/**
 * Represents the account types available on JustBinary.
 * Maps directly to the account switcher dropdown in the nav bar
 * and the balance{demo, real} object managed in the trade UI.
 */
public enum AccountType {

    // ── Account Types ────────────────────────────────────────
    DEMO,   // Simulated account — starts at $10,000, resettable, no real money
    REAL;   // Live funded account — real deposits, real withdrawals

    // ── Classification helpers ───────────────────────────────

    /** Returns true if this is a real/funded account (routes to deposit flow). */
    public boolean isLive() {
        return this == REAL;
    }

    /** Returns true if this is a practice/simulated account. */
    public boolean isSimulated() {
        return this == DEMO;
    }

    /** Returns true if the balance can be reset to default (demo only). */
    public boolean isResettable() {
        return this == DEMO;
    }

    /** Returns true if this account type requires a deposit to trade. */
    public boolean requiresDeposit() {
        return this == REAL;
    }

    // ── Default balance ──────────────────────────────────────

    /**
     * Returns the default starting balance for this account type.
     * Mirrors resetDemo() in the frontend which resets demo to $10,000.
     * Real accounts start at $0.00 until a deposit is made.
     *
     * @return default starting balance in USD
     */
    public double defaultBalance() {
        return switch (this) {
            case DEMO -> 10_000.00;
            case REAL -> 0.00;
        };
    }

    // ── Display helpers ──────────────────────────────────────

    /**
     * Returns the nav label shown in the account switcher dropdown.
     * Matches the acc-label div text set by switchAcc() in the UI.
     */
    public String displayLabel() {
        return switch (this) {
            case DEMO -> "DEMO";
            case REAL -> "REAL";
        };
    }

    /**
     * Returns the flag/avatar emoji shown in the nav balance widget.
     * Mirrors the accFlag logic in switchAcc() — real gets 🇺🇸, demo gets 🅓.
     */
    public String navIcon() {
        return switch (this) {
            case REAL -> "🇺🇸";
            case DEMO -> "🅓";
        };
    }

    /**
     * Returns the dot CSS class used in the dropdown to indicate
     * which account is currently active. Mirrors dot-green / dot-gray logic.
     */
    public String dotClass(AccountType active) {
        return this == active ? "dot-green" : "dot-gray";
    }

    // ── Reverse lookup ───────────────────────────────────────

    /**
     * Parses the lowercase string passed by the frontend
     * switchAcc('demo') / switchAcc('real') into the enum.
     *
     * @throws IllegalArgumentException if the string doesn't match
     */
    public static AccountType fromKey(String key) {
        return switch (key.toLowerCase().trim()) {
            case "demo" -> DEMO;
            case "real" -> REAL;
            default -> throw new IllegalArgumentException("Unknown account type: " + key);
        };
    }
}