package com.itk.wallet.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
        this.version = 0L;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance.setScale(2, java.math.RoundingMode.HALF_EVEN);
    }

}