package com.github.jvalsesia.pcms.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate extends AuditEntity {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal purchaseAmountUSD;

    @Column(nullable = false)
    private BigDecimal exchangeRate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal convertedAmount;

}
