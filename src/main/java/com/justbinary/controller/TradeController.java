package com.justbinary.controller;

import com.justbinary.dto.TradeRequest;
import com.justbinary.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "*")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeTrade(@RequestBody TradeRequest request,
                                        Authentication authentication) {
        try {
            String email   = authentication.getName();
            String tradeId = tradeService.placeTrade(email, request);
            return ResponseEntity.ok(Map.of(
                "message", "Trade placed successfully",
                "tradeId", tradeId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ FIXED — changed @RequestParam to @RequestBody so frontend POST works
    @PostMapping("/settle/{tradeId}")
    public ResponseEntity<?> settleTrade(
            @PathVariable String tradeId,
            @RequestBody Map<String, Boolean> body,
            Authentication authentication) {
        try {
            boolean isWin  = body.getOrDefault("isWin", false);
            String result  = tradeService.settleTrade(tradeId, isWin);
            return ResponseEntity.ok(Map.of(
                "message", result,
                "tradeId", tradeId,
                "isWin",   isWin
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTradeHistory(Authentication authentication) {
        try {
            String email  = authentication.getName();
            String result = tradeService.getTradeHistory(email);
            return ResponseEntity.ok(Map.of("data", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveTrades(Authentication authentication) {
        try {
            String email  = authentication.getName();
            String result = tradeService.getActiveTrades(email);
            return ResponseEntity.ok(Map.of("data", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<?> getTradeById(@PathVariable String tradeId,
                                          Authentication authentication) {
        try {
            String email  = authentication.getName();
            String result = tradeService.getTradeById(email, tradeId);
            return ResponseEntity.ok(Map.of("data", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}