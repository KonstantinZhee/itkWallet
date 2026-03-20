package com.itk.wallet.controller;

import com.itk.wallet.dto.WalletRequest;
import com.itk.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/wallet")
    public ResponseEntity<Void> processTransaction(@Valid @RequestBody WalletRequest request) {
        walletService.processTransaction(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/wallets")
    public ResponseEntity<UUID> createWallet(@RequestParam(required = false) BigDecimal initialBalance) {
        UUID walletId = walletService.createWallet(initialBalance);
        return ResponseEntity.ok(walletId);
    }
}