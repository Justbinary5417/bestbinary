package com.justbinary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── PAYLOAD BUILDER ──────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status, String error, String message, String toast) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     error);
        body.put("message",   message);
        body.put("toast",     toast);
        return ResponseEntity.status(status).body(body);
    }

    // ── TRADE / BALANCE EXCEPTIONS ───────────────────────────────

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalance(
            InsufficientBalanceException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp",       LocalDateTime.now().toString());
        body.put("status",          HttpStatus.PAYMENT_REQUIRED.value());
        body.put("error",           "Insufficient Balance");
        body.put("message",         ex.getMessage());
        body.put("toast",           ex.getToastMessage());
        body.put("userId",          ex.getUserId());
        body.put("requestedStake",  ex.getRequestedStake());
        body.put("currentBalance",  ex.getCurrentBalance());
        body.put("deficit",         ex.getDeficit());
        body.put("balanceDepleted", ex.isBalanceDepleted());
        body.put("canPartiallyFund",ex.canPartiallyFund());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(body);
    }

    @ExceptionHandler(InvalidStakeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStake(
            InvalidStakeException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Stake",
                ex.getMessage(), "⚠️ " + ex.getMessage());
    }

    @ExceptionHandler(TradeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTradeNotFound(
            TradeNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Trade Not Found",
                ex.getMessage(), "❌ Trade not found.");
    }

    @ExceptionHandler(TradeAlreadyResolvedException.class)
    public ResponseEntity<Map<String, Object>> handleTradeAlreadyResolved(
            TradeAlreadyResolvedException ex) {
        return build(HttpStatus.CONFLICT, "Trade Already Resolved",
                ex.getMessage(), "⚠️ This trade is already resolved.");
    }

    @ExceptionHandler(InvalidMarketTabException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMarketTab(
            InvalidMarketTabException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Market Tab",
                ex.getMessage(), "⚠️ Invalid market selected.");
    }

    @ExceptionHandler(InvalidVolIndexException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidVolIndex(
            InvalidVolIndexException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Vol Index",
                ex.getMessage(), "⚠️ Invalid volatility index.");
    }

    // ── AUTO TRADING EXCEPTIONS ──────────────────────────────────

    @ExceptionHandler(AutoTradingAlreadyRunningException.class)
    public ResponseEntity<Map<String, Object>> handleAutoAlreadyRunning(
            AutoTradingAlreadyRunningException ex) {
        return build(HttpStatus.CONFLICT, "Auto Trading Already Running",
                ex.getMessage(), "⚠️ Auto trading is already active.");
    }

    @ExceptionHandler(AutoTradingNotRunningException.class)
    public ResponseEntity<Map<String, Object>> handleAutoNotRunning(
            AutoTradingNotRunningException ex) {
        return build(HttpStatus.CONFLICT, "Auto Trading Not Running",
                ex.getMessage(), "⚠️ No auto trading session is active.");
    }

    @ExceptionHandler(TakeProfitReachedException.class)
    public ResponseEntity<Map<String, Object>> handleTakeProfit(
            TakeProfitReachedException ex) {
        return build(HttpStatus.OK, "Take Profit Reached",
                ex.getMessage(), "🏆 Take profit target hit!");
    }

    @ExceptionHandler(StopLossReachedException.class)
    public ResponseEntity<Map<String, Object>> handleStopLoss(
            StopLossReachedException ex) {
        return build(HttpStatus.OK, "Stop Loss Reached",
                ex.getMessage(), "🛑 Stop loss triggered.");
    }

    // ── ACCOUNT / AUTH EXCEPTIONS ────────────────────────────────

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(
            AccountNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Account Not Found",
                ex.getMessage(), "❌ Account not found.");
    }

    @ExceptionHandler(DemoResetNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleDemoResetNotAllowed(
            DemoResetNotAllowedException ex) {
        return build(HttpStatus.FORBIDDEN, "Demo Reset Not Allowed",
                ex.getMessage(), "❌ Demo reset not allowed on real accounts.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid Credentials",
                "Email or password is incorrect.",
                "❌ Invalid email or password.");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(
            UsernameNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "User Not Found",
                ex.getMessage(), "❌ User not found.");
    }

    // ── WALLET / DEPOSIT EXCEPTIONS ──────────────────────────────

    @ExceptionHandler(DepositFailedException.class)
    public ResponseEntity<Map<String, Object>> handleDepositFailed(
            DepositFailedException ex) {
        return build(HttpStatus.BAD_GATEWAY, "Deposit Failed",
                ex.getMessage(), "❌ Deposit failed. Please try again.");
    }

    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionFailed(
            TransactionFailedException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction Failed",
                ex.getMessage(), "❌ Transaction could not be completed.");
    }

    // ── VALIDATION EXCEPTIONS ────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, Object> body = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.BAD_REQUEST.value());
        body.put("error",     "Validation Failed");
        body.put("message",   "One or more fields are invalid.");
        body.put("toast",     "⚠️ Please check your input.");
        body.put("fields",    fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ── FALLBACK ─────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error",
                "An unexpected error occurred. Please try again.",
                "❌ Something went wrong.");
    }
}