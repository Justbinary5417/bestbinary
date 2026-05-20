package com.justbinary.enums;

/**
 * Represents the trading execution modes available on JustBinary.
 * Maps directly to the AUTO / MANUAL mode toggle buttons in the trade panel
 * and the autoRunning / autoMode state managed by the trade engine.
 */
public enum TradeMode {

    // ── Execution Modes ──────────────────────────────────────
    AUTO,    // Bot continuously fires trades using configured stake, TP, SL, multiplier
    MANUAL;  // User manually taps Even/Odd/Over/Under/Matches/Differs each trade

    // ── Classification helpers ───────────────────────────────

    /** Returns true if the engine should loop trades automatically after each resolution. */
    public boolean isAutomated() {
        return this == AUTO;
    }

    /** Returns true if the user must manually initiate each individual trade. */
    public boolean isManual() {
        return this == MANUAL;
    }

    /**
     * Returns true if auto-trading status panel should be visible.
     * Maps to autoStatus.classList.add('active') in the frontend.
     */
    public boolean showsStatusPanel() {
        return this == AUTO;
    }

    /**
     * Returns true if stop-auto controls (TP, SL, stop button) are enforced.
     * In MANUAL mode these fields exist in the UI but are not actively evaluated
     * after each trade resolution.
     */
    public boolean enforcesTpSl() {
        return this == AUTO;
    }

    // ── Auto-loop delay ──────────────────────────────────────

    /**
     * Returns the millisecond delay between trade resolution and the next
     * auto trade placement. Mirrors the setTimeout(..., 1500) in resolvePosition().
     * Returns 0 for MANUAL since no loop fires.
     *
     * @return delay in milliseconds
     */
    public int loopDelayMs() {
        return switch (this) {
            case AUTO   -> 1500;
            case MANUAL -> 0;
        };
    }

    /**
     * Returns the millisecond delay before a placed trade is resolved.
     * Mirrors the setTimeout(()=>resolvePosition(pos.id), 1200) in placeTrade().
     *
     * @return resolution delay in milliseconds
     */
    public int resolutionDelayMs() {
        return 1200; // same for both modes — tick-based resolution
    }

    // ── Stop conditions ──────────────────────────────────────

    /**
     * Evaluates whether the auto loop should stop based on current P&L,
     * configured take-profit and stop-loss thresholds.
     * Always returns false in MANUAL mode (no loop to stop).
     *
     * @param currentPnl    running P&L since auto session started (can be negative)
     * @param takeProfit    target profit threshold in USD (e.g. 200.0)
     * @param stopLoss      maximum loss threshold in USD, expressed as a positive value (e.g. 999.0)
     * @return true if the auto session should be terminated
     */
    public boolean shouldStop(double currentPnl, double takeProfit, double stopLoss) {
        if (this == MANUAL) return false;
        return currentPnl >= takeProfit || currentPnl <= -stopLoss;
    }

    /**
     * Returns the specific stop reason for display in a toast/alert,
     * mirroring the showToast() messages in resolvePosition().
     * Returns null if the session should continue or mode is MANUAL.
     *
     * @param currentPnl  running P&L since auto session started
     * @param takeProfit  take profit threshold in USD
     * @param stopLoss    stop loss threshold in USD (positive value)
     */
    public StopReason evaluateStopReason(double currentPnl, double takeProfit, double stopLoss) {
        if (this == MANUAL) return null;
        if (currentPnl >= takeProfit)  return StopReason.TAKE_PROFIT_HIT;
        if (currentPnl <= -stopLoss)   return StopReason.STOP_LOSS_HIT;
        return null; // continue trading
    }

    // ── Multiplier application ───────────────────────────────

    /**
     * Applies the stake multiplier for the next auto trade.
     * In MANUAL mode the user sets the stake manually each time,
     * so no multiplier is applied programmatically.
     *
     * @param currentStake  the stake used in the most recent trade
     * @param multiplier    the configured multiplier (e.g. 2x from the UI input)
     * @return the stake to use for the next trade
     */
    public double applyMultiplier(double currentStake, int multiplier) {
        return switch (this) {
            case AUTO   -> currentStake * multiplier;
            case MANUAL -> currentStake; // user controls stake directly
        };
    }

    // ── Display helpers ──────────────────────────────────────

    /**
     * Returns the button label shown in the mode toggle.
     * Mirrors mode-btn text: "● AUTO" and "○ MANUAL".
     */
    public String displayLabel() {
        return switch (this) {
            case AUTO   -> "● AUTO";
            case MANUAL -> "○ MANUAL";
        };
    }

    /**
     * Returns the CSS class applied to the active mode button.
     * Mirrors the classList.toggle('active', ...) logic in setMode().
     */
    public String buttonClass(TradeMode active) {
        return this == active ? "mode-btn active" : "mode-btn";
    }

    // ── Reverse lookup ───────────────────────────────────────

    /**
     * Parses the lowercase string passed by setMode('auto') / setMode('manual')
     * in the frontend into the corresponding TradeMode enum constant.
     *
     * @throws IllegalArgumentException if the string doesn't match
     */
    public static TradeMode fromKey(String key) {
        return switch (key.toLowerCase().trim()) {
            case "auto"   -> AUTO;
            case "manual" -> MANUAL;
            default -> throw new IllegalArgumentException("Unknown trade mode: " + key);
        };
    }

    // ── Nested stop reason enum ──────────────────────────────

    /**
     * Represents the reason an AUTO trading session was terminated.
     * Maps to the toast messages fired in resolvePosition() when
     * TP or SL thresholds are crossed.
     */
    public enum StopReason {

        TAKE_PROFIT_HIT,    // autoPnl >= takeProfit  → "🏆 TAKE PROFIT HIT!"
        STOP_LOSS_HIT,      // autoPnl <= -stopLoss   → "🛑 STOP LOSS HIT!"
        INSUFFICIENT_FUNDS, // balance < stake        → "❌ Insufficient balance"
        MANUALLY_STOPPED;   // user pressed ⏹ STOP AUTO button

        /** Returns the toast message shown to the user in the UI. */
        public String toastMessage(double amount) {
            return switch (this) {
                case TAKE_PROFIT_HIT    -> "🏆 TAKE PROFIT HIT! +$" + String.format("%.2f", amount);
                case STOP_LOSS_HIT      -> "🛑 STOP LOSS HIT! -$"   + String.format("%.2f", Math.abs(amount));
                case INSUFFICIENT_FUNDS -> "❌ Insufficient balance — auto stopped";
                case MANUALLY_STOPPED   -> "⏹ Auto trading stopped";
            };
        }
    }
}