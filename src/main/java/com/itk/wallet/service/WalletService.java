package com.itk.wallet.service;

import com.itk.wallet.dto.WalletRequest;
import com.itk.wallet.exception.InsufficientFundsException;
import com.itk.wallet.exception.WalletNotFoundException;
import com.itk.wallet.model.Wallet;
import com.itk.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void processTransaction(WalletRequest request) {
        int retries = 3;
        while (retries > 0) {
            try {
                Wallet wallet = walletRepository.findByIdWithLock(request.getWalletId())
                        .orElseThrow(() -> new WalletNotFoundException(request.getWalletId()));
                BigDecimal amount = request.getAmount().setScale(2, java.math.RoundingMode.HALF_EVEN);
                if (request.getOperationType() == WalletRequest.OperationType.DEPOSIT) {
                    wallet.setBalance(wallet.getBalance().add(amount));
                    log.info("Deposit successful. Wallet: {}, Amount: {}, New balance: {}",
                            wallet.getId(), amount, wallet.getBalance());
                } else {
                    if (wallet.getBalance().compareTo(amount) < 0) {
                        log.warn("Insufficient funds. Wallet: {}, Balance: {}, Requested: {}",
                                wallet.getId(), wallet.getBalance(), amount);
                        throw new InsufficientFundsException(
                                String.format("Insufficient funds. Current balance: %s, requested: %s",
                                        wallet.getBalance(), amount)
                        );
                    }
                    wallet.setBalance(wallet.getBalance().subtract(amount));
                    log.info("Withdraw successful. Wallet: {}, Amount: {}, New balance: {}",
                            wallet.getId(), amount, wallet.getBalance());
                }
                walletRepository.save(wallet);
                return;
            } catch (OptimisticLockingFailureException e) {
                retries--;
                if (retries == 0) {
                    log.error("Transaction failed after 3 retries for wallet: {}", request.getWalletId());
                    throw new RuntimeException("Transaction failed after retries", e);
                }
                log.debug("Optimistic lock exception, retries left: {}", retries);
            }
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        return wallet.getBalance();
    }

    @Transactional
    public UUID createWallet(BigDecimal initialBalance) {
        Wallet wallet = new Wallet();
        if (initialBalance != null) {
            wallet.setBalance(initialBalance);
        }
        wallet = walletRepository.save(wallet);
        log.info("Wallet created with id: {}, balance: {}", wallet.getId(), wallet.getBalance());
        return wallet.getId();
    }
}