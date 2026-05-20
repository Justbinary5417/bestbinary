package com.justbinary.enums;

/**
 * Represents the Volatility Index instruments available on JustBinary.
 * Maps directly to the vol-chip strip built by buildVolRow() in the trade UI.
 * Each index has two tick modes: ONE_SECOND (1s) or TICK (Tk).
 */
public enum VolIndex {

    // ── Volatility Indices ───────────────────────────────────
    VOL_10  ("10",  10),
    VOL_20  ("20",  20),
    VOL_30  ("30",  30),
    VOL_40  ("40",  40),
    VOL_50  ("50",  50),
    VOL_60  ("60",  60),
    VOL_70  ("70",  70),
    VOL_80  ("80",  80),
    VOL_90  ("90",  90);

    // ── Fields ───────────────────────────────────────────────
    private final String key;         // matches vols[] array string values: "10","20"...
    private final int    volatility;  // numeric volatility level

    VolIndex(String key, int volatility) {
        this.key        = key;
        this.volatility = volatility;
    }

    public String key()        { return key; }
    public int    volatility() { return volatility; }

    // ── Tick Mode (per-vol state) ────────────────────────────

    /**
     * Represents the two tick modes toggled per vol chip in the UI.
     * Maps to the volState{} object and the 1s / Tk toggle buttons
     * inside each .vol-chip rendered by buildVolRow().
     */
    public enum TickMode {

        ONE_SECOND, // "1s" button — volState[v] = true  — price updates every 1 second
        TICK;       // "Tk" button — volState[v] = false — price updates every raw tick

        /**
         * Returns the button label shown inside the vol chip toggle.
         * Mirrors the .vt-btn text in buildVolRow().
         */
        public String displayLabel() {
            return switch (this) {
                case ONE_SECOND -> "1s";
                case TICK       -> "Tk";
            };
        }

        /**
         * Returns the CSS class for this tick mode button given the active mode.
         * Mirrors: className='vt-btn'+(is1s?' sel':'') in buildVolRow().
         */
        public String btnClass(TickMode active) {
            return this == active ? "vt-btn sel" : "vt-btn";
        }

        /**
         * Parses the frontend boolean volState value into a TickMode.
         * volState[v] = true  → ONE_SECOND
         * volState[v] = false → TICK
         */
        public static TickMode fromBool(boolean is1s) {
            return is1s ? ONE_SECOND : TICK;
        }

        /**
         * Converts back to the boolean used by volState{} in the frontend.
         * ONE_SECOND → true, TICK → false.
         */
        public boolean toVolStateBool() {
            return this == ONE_SECOND;
        }
    }

    // ── Symbol label ─────────────────────────────────────────

    /**
     * Returns the chart symbol label shown in .chart-symbol div.
     * Mirrors updateSymbolLabel():
     *   'Vol ' + activeVol + (volState[activeVol] ? ' (1s)' : '')
     *
     * @param mode  the current tick mode for this index
     * @return e.g. "Vol 10 (1s)" or "Vol 75"
     */
    public String symbolLabel(TickMode mode) {
        return switch (mode) {
            case ONE_SECOND -> "Vol " + key + " (1s)";
            case TICK       -> "Vol " + key;
        };
    }

    /**
     * Returns the default symbol label assuming ONE_SECOND mode.
     * Matches the initial chartSymbolLabel on page load: "Vol 10 (1s)".
     */
    public String defaultSymbolLabel() {
        return symbolLabel(TickMode.ONE_SECOND);
    }

    // ── Toast message ────────────────────────────────────────

    /**
     * Returns the toast message shown when switching to this vol index.
     * Mirrors: showToast('📊 Switched to Vol ' + v + (volState[v] ? ' (1s)' : ''))
     * called inside selectVol() in the frontend.
     *
     * @param mode  the tick mode currently set for this index
     * @return e.g. "📊 Switched to Vol 50 (1s)"
     */
    public String switchToast(TickMode mode) {
        return "📊 Switched to " + symbolLabel(mode);
    }

    // ── Chip CSS helpers ─────────────────────────────────────

    /**
     * Returns the CSS class for the vol chip container.
     * Mirrors: className='vol-chip'+(v===activeVol?' active':'') in buildVolRow().
     */
    public String chipClass(VolIndex active) {
        return this == active ? "vol-chip active" : "vol-chip";
    }

    /**
     * Returns the CSS class for the vol number label inside the chip.
     * Active chips get var(--accent) color via .vol-chip.active .vol-num.
     */
    public String numClass(VolIndex active) {
        return this == active ? "vol-num active" : "vol-num";
    }

    // ── Chip DOM ID ──────────────────────────────────────────

    /**
     * Returns the element ID assigned to each vol chip div.
     * Mirrors: chip.id = 'vchip-' + v in buildVolRow().
     * e.g. "vchip-10", "vchip-50"
     */
    public String chipDomId() {
        return "vchip-" + key;
    }

    // ── Classification helpers ───────────────────────────────

    /** Returns true if this is the default index loaded on page init. */
    public boolean isDefault() {
        return this == VOL_10;
    }

    /**
     * Returns true if this index has low volatility (10–30).
     * Lower vol = slower, steadier price movement = better for MATCHES.
     */
    public boolean isLowVolatility() {
        return volatility <= 30;
    }

    /**
     * Returns true if this index has medium volatility (40–60).
     * Medium vol = balanced — suits EVEN/ODD and OVER/UNDER.
     */
    public boolean isMediumVolatility() {
        return volatility >= 40 && volatility <= 60;
    }

    /**
     * Returns true if this index has high volatility (70–90).
     * Higher vol = rapid, erratic price movement = better for DIFFERS.
     */
    public boolean isHighVolatility() {
        return volatility >= 70;
    }

    /**
     * Returns the AI Scanner recommendation hint for this index.
     * Mirrors the random msgs[] array inside aiScan() in the frontend —
     * provides a deterministic, volatility-aware suggestion per index.
     */
    public String aiScanHint() {
        if (isLowVolatility())    return "🤖 AI: Low volatility — try Matches";
        if (isMediumVolatility()) return "🤖 AI: Even/Odd market looks favorable";
        return                           "🤖 AI: High volatility — try Differs";
    }

    // ── Price simulation ─────────────────────────────────────

    /**
     * Returns the maximum price move per tick for this index.
     * Mirrors the chart tick engine:
     *   const move = (Math.random() - 0.495) * 3.5
     * Scaled by volatility level so higher-index symbols move faster.
     *
     * @return max absolute tick move in price units
     */
    public double maxTickMove() {
        return (volatility / 10.0) * 0.35;
    }

    // ── Reverse lookup ───────────────────────────────────────

    /**
     * Parses the string key used in the frontend vols[] array ("10", "20" ... "90")
     * into the corresponding VolIndex enum constant.
     *
     * @throws IllegalArgumentException if the key doesn't match any index
     */
    public static VolIndex fromKey(String key) {
        for (VolIndex v : values()) {
            if (v.key.equals(key.trim())) return v;
        }
        throw new IllegalArgumentException("Unknown vol index key: " + key);
    }

    /**
     * Returns the default VolIndex loaded on page init.
     * Mirrors: let activeVol = '10' at the top of the script.
     */
    public static VolIndex defaultIndex() {
        return VOL_10;
    }
}