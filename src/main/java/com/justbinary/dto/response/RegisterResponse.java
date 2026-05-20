package com.justbinary.dto.response;

import com.justbinary.enums.AccountType;
import com.justbinary.enums.CurrencyType;

import java.time.LocalDateTime;

/**
 * ════════════════════════════════════════════════════════
 *  RegisterResponse.java
 *  com.justbinary.dto.response
 *
 *  Returned by:
 *    POST /api/auth/register
 *
 *  Contains:
 *    - JWT token (auto-logged in after register)
 *    - Full user profile
 *    - Initial wallet balances
 *    - Success message
 * ════════════════════════════════════════════════════════
 */
public class RegisterResponse {

    // ── Auth Token ────────────────────────────────────
    private String token;
    private String tokenType = "Bearer";
    private long   expiresIn;   // milliseconds (matches jwt.expiration)

    // ── User Profile ──────────────────────────────────
    private Long   userId;
    private String username;
    private String email;
    private String phone;        // optional, for M-Pesa
    private String role;         // "USER" | "ADMIN"

    // ── Wallet ────────────────────────────────────────
    private double      demoBalance;   // default: 10000.00
    private double      realBalance;   // default: 0.00
    private CurrencyType currency;     // USD by default
    private AccountType  activeAccount; // DEMO by default

    // ── Timestamps ───────────────────────────────────
    private LocalDateTime registeredAt;

    // ── Message ──────────────────────────────────────
    private boolean success;
    private String  message;

    // ═══════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════

    public RegisterResponse() {}

    /** Full constructor */
    public RegisterResponse(
            String token,
            long expiresIn,
            Long userId,
            String username,
            String email,
            String phone,
            String role,
            double demoBalance,
            double realBalance,
            CurrencyType currency,
            AccountType activeAccount,
            LocalDateTime registeredAt
    ) {
        this.token         = token;
        this.expiresIn     = expiresIn;
        this.userId        = userId;
        this.username      = username;
        this.email         = email;
        this.phone         = phone;
        this.role          = role;
        this.demoBalance   = demoBalance;
        this.realBalance   = realBalance;
        this.currency      = currency;
        this.activeAccount = activeAccount;
        this.registeredAt  = registeredAt;
        this.success       = true;
        this.message       = "Registration successful! Welcome to JustBinary.";
    }

    // ═══════════════════════════════════════════════════
    // STATIC FACTORY METHODS
    // ═══════════════════════════════════════════════════

    /**
     * Call this in AuthService.register() after saving user + wallet.
     *
     * Example usage in AuthService:
     *
     *   String token = jwtUtil.generateToken(user.getEmail());
     *   return RegisterResponse.success(
     *       token,
     *       86400000L,          // from application.properties jwt.expiration
     *       user.getId(),
     *       user.getUsername(),
     *       user.getEmail(),
     *       user.getPhone(),
     *       user.getRole(),
     *       wallet.getDemoBalance(),
     *       wallet.getRealBalance(),
     *       CurrencyType.USD,
     *       AccountType.DEMO,
     *       user.getCreatedAt()
     *   );
     */
    public static RegisterResponse success(
            String token,
            long expiresIn,
            Long userId,
            String username,
            String email,
            String phone,
            String role,
            double demoBalance,
            double realBalance,
            CurrencyType currency,
            AccountType activeAccount,
            LocalDateTime registeredAt
    ) {
        return new RegisterResponse(
                token, expiresIn, userId, username, email,
                phone, role, demoBalance, realBalance,
                currency, activeAccount, registeredAt
        );
    }

    /**
     * Call this when registration fails (e.g. email already exists).
     * Note: better to throw a custom exception and let
     * GlobalExceptionHandler return this, but useful for simple cases.
     */
    public static RegisterResponse failure(String reason) {
        RegisterResponse r = new RegisterResponse();
        r.success = false;
        r.message = reason;
        return r;
    }

    // ═══════════════════════════════════════════════════
    // GETTERS & SETTERS
    // ═══════════════════════════════════════════════════

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getDemoBalance() { return demoBalance; }
    public void setDemoBalance(double demoBalance) { this.demoBalance = demoBalance; }

    public double getRealBalance() { return realBalance; }
    public void setRealBalance(double realBalance) { this.realBalance = realBalance; }

    public CurrencyType getCurrency() { return currency; }
    public void setCurrency(CurrencyType currency) { this.currency = currency; }

    public AccountType getActiveAccount() { return activeAccount; }
    public void setActiveAccount(AccountType activeAccount) { this.activeAccount = activeAccount; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // ═══════════════════════════════════════════════════
    // toString
    // ═══════════════════════════════════════════════════
    @Override
    public String toString() {
        return "RegisterResponse{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", demoBalance=" + demoBalance +
                ", success=" + success +
                ", registeredAt=" + registeredAt +
                '}';
    }
}