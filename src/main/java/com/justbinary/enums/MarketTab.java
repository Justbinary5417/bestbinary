package com.justbinary.enums;

import java.util.List;

/**
 * Represents the three market tab categories available on JustBinary.
 * Maps directly to the .market-tabs strip rendered in the nav area,
 * controlled by setTab() and renderPayoutButtons() in the trade UI.
 */
public enum MarketTab {

    // ── Market Tabs ──────────────────────────────────────────
    EVEN_ODD,        // tab-eo  → ⚡ Even/Odd    — default active tab on load
    OVER_UNDER,      // tab-ou  → 📊 Over/Under
    MATCHES_DIFFERS; // tab-md  → 🎯 Matches/Differs

    // ── Classification helpers ───────────────────────────────

    /** Returns true if this is the default tab loaded on page init. */
    public boolean isDefault() {
        return this == EVEN_ODD;
    }

    /**
     * Returns true if predicting this tab requires tracking the entry digit.
     * MATCHES_DIFFERS needs entryDigit to resolve; the others only need lastDigit.
     * Mirrors the pos.entryDigit reference in resolvePosition() switch block.
     */
    public boolean requiresEntryDigit() {
        return this == MATCHES_DIFFERS;
    }

    /**
     * Returns true if the outcome is purely based on digit parity.
     * Used to decide if the digit-parity fast-path can be used server-side.
     */
    public boolean isParityBased() {
        return this == EVEN_ODD;
    }

    /**
     * Returns true if the outcome is based on digit range threshold (> 4 or < 5).
     * Mirrors the OVER / UNDER resolution logic in resolvePosition().
     */
    public boolean isThresholdBased() {
        return this == OVER_UNDER;
    }

    /**
     * Returns true if the outcome depends on digit identity matching.
     * Mirrors the MATCHES / DIFFERS resolution logic in resolvePosition().
     */
    public boolean isIdentityBased() {
        return this == MATCHES_DIFFERS;
    }

    // ── Tab trade types ──────────────────────────────────────

    /**
     * Returns the two TradeType constants that belong to this tab.
     * Mirrors the two buttons rendered by renderPayoutButtons() per tab.
     * Order is: [left button, right button] as displayed in the UI.
     */
    public List<TradeType> tradeTypes() {
        return switch (this) {
            case EVEN_ODD        -> List.of(TradeType.EVEN,    TradeType.ODD);
            case OVER_UNDER      -> List.of(TradeType.OVER,    TradeType.UNDER);
            case MATCHES_DIFFERS -> List.of(TradeType.MATCHES, TradeType.DIFFERS);
        };
    }

    /**
     * Returns the left/primary trade type button for this tab.
     * e.g. EVEN for EVEN_ODD, OVER for OVER_UNDER, MATCHES for MATCHES_DIFFERS.
     */
    public TradeType primaryType() {
        return tradeTypes().get(0);
    }

    /**
     * Returns the right/secondary trade type button for this tab.
     * e.g. ODD for EVEN_ODD, UNDER for OVER_UNDER, DIFFERS for MATCHES_DIFFERS.
     */
    public TradeType secondaryType() {
        return tradeTypes().get(1);
    }

    // ── Display helpers ──────────────────────────────────────

    /**
     * Returns the full tab label shown in the market-tabs strip.
     * Mirrors the text content of each .mtab div in the HTML.
     */
    public String displayLabel() {
        return switch (this) {
            case EVEN_ODD        -> "⚡ Even/Odd";
            case OVER_UNDER      -> "📊 Over/Under";
            case MATCHES_DIFFERS -> "🎯 Matches/Differs";
        };
    }

    /**
     * Returns the HTML element ID of this tab's button in the DOM.
     * Mirrors the id attributes: tab-eo, tab-ou, tab-md.
     */
    public String domId() {
        return switch (this) {
            case EVEN_ODD        -> "tab-eo";
            case OVER_UNDER      -> "tab-ou";
            case MATCHES_DIFFERS -> "tab-md";
        };
    }

    /**
     * Returns the CSS classes for this tab button given the currently active tab.
     * Mirrors the classList.toggle('active', t===tab) logic in setTab().
     */
    public String tabClass(MarketTab active) {
        return this == active ? "mtab active" : "mtab";
    }

    /**
     * Returns the left payout button CSS class for this tab.
     * Mirrors the btn-even / btn-over / btn-matches classes in renderPayoutButtons().
     */
    public String primaryBtnClass() {
        return switch (this) {
            case EVEN_ODD        -> "pay-btn btn-even";
            case OVER_UNDER      -> "pay-btn btn-over";
            case MATCHES_DIFFERS -> "pay-btn btn-matches";
        };
    }

    /**
     * Returns the right payout button CSS class for this tab.
     * Mirrors the btn-odd / btn-under / btn-differs classes in renderPayoutButtons().
     */
    public String secondaryBtnClass() {
        return switch (this) {
            case EVEN_ODD        -> "pay-btn btn-odd";
            case OVER_UNDER      -> "pay-btn btn-under";
            case MATCHES_DIFFERS -> "pay-btn btn-differs";
        };
    }

    /**
     * Returns the threshold label displayed on the Over/Under buttons.
     * Mirrors "Over 4" and "Under 5" in renderPayoutButtons().
     * Returns null for tabs that don't use a threshold label.
     */
    public String thresholdLabel() {
        return switch (this) {
            case OVER_UNDER      -> "4/5"; // Over 4 | Under 5
            case EVEN_ODD,
                 MATCHES_DIFFERS -> null;
        };
    }

    // ── Payout calculation ───────────────────────────────────

    /**
     * Calculates the displayed payout for a given stake.
     * Mirrors: const payout = (stake * 1.95).toFixed(2) in renderPayoutButtons().
     * Payout multiplier is uniform (1.95x) across all tabs on JustBinary.
     *
     * @param stake  the trade stake in USD
     * @return formatted payout string (e.g. "$19.50")
     */
    public String formattedPayout(double stake) {
        return String.format("$%.2f", stake * 1.95);
    }

    /**
     * Returns the raw payout amount for a given stake.
     *
     * @param stake  the trade stake in USD
     * @return payout amount in USD
     */
    public double payoutAmount(double stake) {
        return stake * 1.95;
    }

    // ── Reverse lookup ───────────────────────────────────────

    /**
     * Parses the frontend tab key string used in setTab('eo') / setTab('ou') / setTab('md')
     * into the corresponding MarketTab enum constant.
     *
     * @throws IllegalArgumentException if the key doesn't match any tab
     */
    public static MarketTab fromKey(String key) {
        return switch (key.toLowerCase().trim()) {
            case "eo"  -> EVEN_ODD;
            case "ou"  -> OVER_UNDER;
            case "md"  -> MATCHES_DIFFERS;
            default    -> throw new IllegalArgumentException("Unknown market tab key: " + key);
        };
    }

    /**
     * Resolves the MarketTab that owns a given TradeType.
     * Useful for routing incoming trade requests to the correct tab context.
     *
     * @param type  the TradeType to look up
     * @return the MarketTab that contains that TradeType
     */
    public static MarketTab fromTradeType(TradeType type) {
        return switch (type) {
            case EVEN, ODD         -> EVEN_ODD;
            case OVER, UNDER       -> OVER_UNDER;
            case MATCHES, DIFFERS  -> MATCHES_DIFFERS;
        };
    }
}