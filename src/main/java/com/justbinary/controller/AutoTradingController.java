package com.justbinary.controller;

import com.justbinary.service.AutoTradingService;
import com.justbinary.service.AutoTradingService.AutoSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auto-trading")
public class AutoTradingController {

    private final AutoTradingService autoTradingService;

    public AutoTradingController(AutoTradingService autoTradingService) {
        this.autoTradingService = autoTradingService;
    }

    // ─── START AUTO TRADING ───────────────────────────────────
    @PostMapping("/start")
    public ResponseEntity<?> startAutoTrading(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            String userId   = authentication.getName();
            String tradeType = (String) request.get("tradeType");
            String market    = (String) request.get("market");
            double stake     = Double.parseDouble(request.get("stake").toString());
            double takeProfit= Double.parseDouble(request.get("takeProfit").toString());
            double stopLoss  = Double.parseDouble(request.get("stopLoss").toString());

            AutoSession session = autoTradingService.startAutoTrading(
                    userId, tradeType, market, stake, takeProfit, stopLoss);

            return ResponseEntity.ok(buildSessionResponse(session, "Auto trading started"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── STOP AUTO TRADING ────────────────────────────────────
    @PostMapping("/stop")
    public ResponseEntity<?> stopAutoTrading(Authentication authentication) {
        try {
            String userId = authentication.getName();
            AutoSession session = autoTradingService.stopAutoTrading(userId);
            return ResponseEntity.ok(buildSessionResponse(session, "Auto trading stopped"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── GET SESSION STATUS ───────────────────────────────────
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(Authentication authentication) {
        try {
            String userId = authentication.getName();
            AutoSession session = autoTradingService.getSessionStatus(userId);
            return ResponseEntity.ok(buildSessionResponse(session, "Session active"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── IS ACTIVE CHECK ──────────────────────────────────────
    @GetMapping("/active")
    public ResponseEntity<?> isActive(Authentication authentication) {
        try {
            String userId  = authentication.getName();
            boolean active = autoTradingService.isAutoTradingActive(userId);
            return ResponseEntity.ok(Map.of("active", active));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── RECORD TRADE RESULT ──────────────────────────────────
    @PostMapping("/record")
    public ResponseEntity<?> recordTradeResult(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            double payout = Double.parseDouble(request.get("payout").toString());
            double stake  = Double.parseDouble(request.get("stake").toString());
            boolean won   = Boolean.parseBoolean(request.get("won").toString());

            AutoSession session = autoTradingService.recordTradeResult(
                    userId, payout, stake, won);

            return ResponseEntity.ok(buildSessionResponse(session, "Trade recorded"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── HELPER ───────────────────────────────────────────────
    private Map<String, Object> buildSessionResponse(AutoSession session, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message",        message);
        response.put("userId",         session.getUserId());
        response.put("tradeType",      session.getTradeType());
        response.put("market",         session.getMarket());
        response.put("stake",          session.getStake());
        response.put("takeProfit",     session.getTakeProfit());
        response.put("stopLoss",       session.getStopLoss());
        response.put("currentBalance", session.getCurrentBalance());
        response.put("pnl",            session.getPnl());
        response.put("running",        session.isRunning());
        response.put("stopReason",     session.getStopReason());
        return response;
    }
}